package com.example.myapplication.domain.usecase.asset

import android.util.Log
import com.example.myapplication.data.model.AssetData
import com.example.myapplication.data.parser.AssetParser
import com.example.myapplication.domain.reopsitory.EthereumRepository
import javax.inject.Inject

class GetAssetsUseCase @Inject constructor(
    private val repository: EthereumRepository,
    private val parser: AssetParser
) {
    suspend operator fun invoke(): List<AssetData> {
        val assets = repository.getAssets()
        return parser.parseAssets(assets)
    }
}