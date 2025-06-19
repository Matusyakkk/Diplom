package com.example.myapplication.ui.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.AssetData
import com.example.myapplication.domain.usecase.asset.AssetUseCases
import com.example.myapplication.ui.navigation.NavigationEvent
import com.example.myapplication.ui.navigation.NavigationManager
import com.example.myapplication.ui.viewmodel.base.ViewModelWithLoading
import com.example.myapplication.ui.viewmodel.base.launchWithLoading
import com.example.myapplication.utils.AssetUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.io.File
import java.math.BigInteger
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class AssetViewModel @Inject constructor(
    private val assetUseCases: AssetUseCases,
    internal val navigationManager: NavigationManager,
) : ViewModel(), ViewModelWithLoading {

    private val _assets = MutableStateFlow<List<AssetData>>(emptyList())
    val assets: StateFlow<List<AssetData>> = _assets.asStateFlow()

    override var isLoading by mutableStateOf(false)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    val filteredAssets: StateFlow<List<AssetData>> = combine(
        _searchQuery, _assets
    ) { query, assets ->
        assets.filter {
            if (it.auctionEndTime > BigInteger.ZERO &&
                BigInteger.valueOf(Instant.now().epochSecond) > it.auctionEndTime) {
                Log.i("FILTER_FINALIZE", "FINALIZE TOKEN ${it.assetId} FROM AUC BY ${it.buyoutPrice} for ${it.auctionEndTime} current time ${BigInteger.valueOf(Instant.now().epochSecond)}")
                finalizeAuction(it.assetId)
            }
            (it.auctionEndTime > BigInteger.ZERO && BigInteger.valueOf(Instant.now().epochSecond) < it.auctionEndTime) &&
                    (it.name.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var isInitialized by mutableStateOf(false)

    fun initializeIfNeeded() {
        if (!isInitialized) {
            loadAssets()
            isInitialized = true
        }
    }

    fun loadAssets() = launchWithLoading {
        _assets.value = assetUseCases.getAssets()
        Log.i("FETCH", "_parsedAssets: ${_assets.value.size}")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun finalizeAuction(assetId: BigInteger) = launchWithLoading {
        Log.d("FINALIZE", "FINALIZE AUCTION FOR ASSET $assetId")
        val assetbyID = findById(assetId)
        Log.i("FILTER_FINALIZE", "FINALIZE TOKEN ${assetbyID?.assetId} FROM AUC BY ${assetbyID?.buyoutPrice} for ${assetbyID?.auctionEndTime} current time ${BigInteger.valueOf(Instant.now().epochSecond)}")
        assetUseCases.finalizeAuction(findById(assetId))
    }

    fun createAndUploadAsset(imageFile: File, name: String, description: String) = launchWithLoading {
        assetUseCases.createAndUploadAsset(imageFile, name, description)
        loadAssets()
        navigateToProfile()
    }

    fun executeAssetBuyout(assetId: BigInteger, buyoutAmount: BigInteger) = launchWithLoading {
        assetUseCases.executeAssetBuyout(assetId, buyoutAmount)
        loadAssets()
        navigateToDetail(assetId)
    }

    fun placeAssetBid(assetId: BigInteger, amount: BigInteger) = launchWithLoading {
        assetUseCases.placeAssetBid(assetId, amount)
        loadAssets()
        navigateToDetail(assetId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun listAssetForAuction(
        assetId: BigInteger,
        buyoutPrice: BigInteger,
        auctionEndTime: BigInteger
    ) = launchWithLoading {
        Log.i("LISTING", "LISTING TOKEN $assetId FOR AUC BY $buyoutPrice for $auctionEndTime")
        assetUseCases.listAssetForAuction(assetId, buyoutPrice, auctionEndTime)
        //loadAssets()
        navigateToDetail(assetId)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun navigateToDetail(assetId: BigInteger) {
        navigationManager.navigate(NavigationEvent.GoToAssetDetailScreen(assetId.toString()))
    }

    fun navigateToListAsset(assetId: BigInteger) {
        navigationManager.navigate(NavigationEvent.GoToListAssetScreen(assetId.toString()))
    }

    fun navigateToMakeBid(assetId: BigInteger) {
        navigationManager.navigate(NavigationEvent.GoToMakeBidScreen(assetId.toString()))
    }

    fun navigateToBuyOut(assetId: BigInteger) {
        navigationManager.navigate(NavigationEvent.GoToBuyOutScreen(assetId.toString()))
    }

    fun navigateToCreateItem() {
        navigationManager.navigate(NavigationEvent.GoToCreateAssetScreen)
    }

    fun navigateToProfile() {
        navigationManager.navigate(NavigationEvent.GoToProfileScreen)
    }

    fun navigateToAssetsForSale() {
        navigationManager.navigate(NavigationEvent.GoToAssetsForSaleScreen)
    }

    fun findById(assetId: BigInteger) = _assets.value.find { it.assetId == assetId }

    fun findAssetsOwnedByUser(address: String): List<AssetData> =
        AssetUtils.getOwnedAssets(_assets.value, address)

    fun findAssetsListedByUser(address: String): List<AssetData> =
        AssetUtils.getListedAssets(_assets.value, address)

    fun findAssetsByHighestBidder(address: String): List<AssetData> =
        AssetUtils.getHighestBidAssets(_assets.value, address)

    fun isUserOwnerOrHighestBidder(asset: AssetData?, address: String): Boolean =
        AssetUtils.isNotOwnerOrHighestBidder(asset, address)
}
