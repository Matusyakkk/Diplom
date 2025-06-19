// SPDX-License-Identifier: MIT
pragma solidity 0.8.30;

import "@openzeppelin/contracts/token/ERC721/extensions/ERC721URIStorage.sol";
import "@openzeppelin/contracts/utils/Counters.sol";

contract MyTokenizedAssets is ERC721URIStorage {

    using Counters for Counters.Counter;
    Counters.Counter private _assetIds;

    struct Asset {
        uint256 assetId;
        string metaDataUri;
        address owner;
        uint256 buyoutPrice; //in wei
        uint256 auctionEndTime; //in seconds
        uint256 highestBid;
        address highestBidder;
    }

    mapping (uint256 => Asset) public assets;
    uint256[] private _allAssetIds;

    constructor() ERC721("MyTokenizedAssets", "MTA") {}

    event AssetMinted(uint256 assetId, string uri, address owner);
    event AssetListedForAuction(uint256 assetId, uint256 buyoutPrice, uint256 auctionDuration);
    event BidPlaced(uint256 assetId, address bidder, uint256 amount);
    event AssetBought(uint256 assetId, address buyer, uint256 amount);

    function mintAsset(string memory uri) external {
        _assetIds.increment();
        uint256 newAssetId = _assetIds.current();

        _mint(msg.sender, newAssetId);
        _setTokenURI(newAssetId, uri);

        assets[newAssetId] = Asset({
            assetId: newAssetId,
            metaDataUri: uri,
            owner: msg.sender,
            buyoutPrice: 0,
            auctionEndTime: 0,
            highestBid: 0,
            highestBidder: address(0)
        });
        _allAssetIds.push(newAssetId);

        emit AssetMinted(newAssetId, uri, msg.sender);
    }

    function placeBid(uint256 assetId) external payable {
        require(_exists(assetId), "Asset does not exist");
        require(block.timestamp < assets[assetId].auctionEndTime, "Auction has ended");
        require(msg.value > assets[assetId].highestBid, "Bid must be higher than current highest bid");
        require(msg.sender != assets[assetId].owner, "Owner cannot bid on own asset");

        // Refund previous highest bidder
        if (assets[assetId].highestBidder != address(0)) {
            payable(assets[assetId].highestBidder).transfer(assets[assetId].highestBid);
        }

        assets[assetId].highestBid = msg.value;
        assets[assetId].highestBidder = msg.sender;

        emit BidPlaced(assetId, msg.sender, msg.value);
    }

    function buyout(uint256 assetId) external payable {
        require(_exists(assetId), "Asset does not exist");
        require(block.timestamp < assets[assetId].auctionEndTime, "Auction has ended");
        require(msg.value >= assets[assetId].buyoutPrice, "Insufficient funds for buyout");

        address previousOwner = assets[assetId].owner;

        //Transfer Asset ownership
        _transfer(previousOwner, msg.sender, assetId);
        assets[assetId].owner = msg.sender;

        //End Auction
        assets[assetId].auctionEndTime = 0;

        // Refund highest bidder
        if (assets[assetId].highestBidder != address(0)) {
            payable(assets[assetId].highestBidder).transfer(assets[assetId].highestBid);
        }

        // Transfer buyout amount to previous owner
        payable(previousOwner).transfer(msg.value);

        emit AssetBought(assetId, msg.sender, msg.value);
    }

    function listAssetForAuction(uint256 assetId, uint256 buyoutPrice, uint256 auctionDuration) external {
        require(_exists(assetId), "Asset does not exist");
        require(ownerOf(assetId) == msg.sender, "Only owner can list asset for auction");
        require(assets[assetId].auctionEndTime == 0, "Asset is already listed for auction");
        require(auctionDuration > 0, "Auction duration must be greater than 0");

        assets[assetId].buyoutPrice = buyoutPrice;
        assets[assetId].auctionEndTime = block.timestamp + auctionDuration;
        assets[assetId].highestBid = 0;
        assets[assetId].highestBidder = address(0);

        emit AssetListedForAuction(assetId, buyoutPrice, auctionDuration);
    }

    function finalizeAuction(uint256 assetId) external {
        require(_exists(assetId), "Asset does not exist");
        Asset storage asset = assets[assetId];

        require(asset.auctionEndTime > 0, "Auction was not started");

        address previousOwner = asset.owner;

        if (asset.highestBidder != address(0)) {
            address winner = asset.highestBidder;
            uint256 winningBid = asset.highestBid;

            _transfer(previousOwner, winner, assetId);
            asset.owner = winner;

            payable(previousOwner).transfer(winningBid);

            emit AssetBought(assetId, winner, winningBid);
        }

        asset.auctionEndTime = 0;
        asset.highestBid = 0;
        asset.highestBidder = address(0);
        asset.buyoutPrice = 0;
    }


    function getAllAssets() external  view returns (Asset[] memory) {
        uint256 total = _allAssetIds.length;
        Asset[] memory allAssets = new Asset[](total);

        for (uint256 i = 0; i < total; i++) {
            uint256 assetId = _allAssetIds[i];
            allAssets[i] = assets[assetId];
        }

        return allAssets;
    }

    function _exists(uint assetId) internal view returns(bool) {
        return _ownerOf(assetId) != address(0);
    }

    function getETHBalance(address _address) public view returns (uint256) {
        return _address.balance;
    }
}