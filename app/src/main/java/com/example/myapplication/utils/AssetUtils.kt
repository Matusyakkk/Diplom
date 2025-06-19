package com.example.myapplication.utils

import com.example.myapplication.data.model.AssetData
import java.math.BigInteger

object AssetUtils {

    /**
     * Повертає всі активи, якими володіє користувач, але які не виставлені на аукціон.
     */
    fun getOwnedAssets(assets: List<AssetData>, address: String): List<AssetData> {
        return assets.filter {
            it.owner.equals(address, ignoreCase = true) &&
                    it.auctionEndTime == BigInteger.ZERO
        }
    }

    /**
     * Повертає всі активи, які користувач виставив на аукціон.
     */
    fun getListedAssets(assets: List<AssetData>, address: String): List<AssetData> {
        return assets.filter {
            it.owner.equals(address, ignoreCase = true) &&
                    it.auctionEndTime > BigInteger.ZERO
        }
    }

    /**
     * Повертає всі активи, на які користувач зробив найвищу ставку.
     */
    fun getHighestBidAssets(assets: List<AssetData>, address: String): List<AssetData> {
        return assets.filter {
            it.highestBidder.equals(address, ignoreCase = true)
        }
    }

    /**
     * Чи користувач не є ні власником, ні найвищим ставником.
     */
    fun isNotOwnerOrHighestBidder(asset: AssetData?, address: String): Boolean {
        return asset?.let {
            it.owner != address && it.highestBidder != address
        } ?: true
    }
}