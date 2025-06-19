package com.example.myapplication.di

import android.content.Context
import com.example.myapplication.data.parser.AssetParser
import com.example.myapplication.data.parser.AssetParserImpl
import com.example.myapplication.data.repository.EthereumRepositoryImpl
import com.example.myapplication.data.service.PinataServiceImpl
import com.example.myapplication.domain.reopsitory.EthereumRepository
import com.example.myapplication.domain.service.PinataService
import com.example.myapplication.domain.usecase.asset.AssetUseCases
import com.example.myapplication.domain.usecase.asset.BuyoutUseCase
import com.example.myapplication.domain.usecase.asset.CreateAssetUseCase
import com.example.myapplication.domain.usecase.asset.FinalizeAuctionUseCase
import com.example.myapplication.domain.usecase.asset.GetAssetsUseCase
import com.example.myapplication.domain.usecase.asset.ListAssetUseCase
import com.example.myapplication.domain.usecase.asset.PlaceBidUseCase
import com.example.myapplication.domain.usecase.wallet.ConnectWalletUseCase
import com.example.myapplication.domain.usecase.wallet.ConnectWithoutWalletUseCase
import com.example.myapplication.domain.usecase.wallet.GetBalanceUseCase
import com.example.myapplication.domain.usecase.wallet.LogoutUseCase
import com.example.myapplication.domain.usecase.wallet.WalletUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.metamask.androidsdk.DappMetadata
import io.metamask.androidsdk.Ethereum
import io.metamask.androidsdk.SDKOptions
import okhttp3.OkHttpClient
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.StaticGasProvider
import java.math.BigInteger
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class Module {
    @Provides
    @Singleton
    fun provideEthereum(
        @ApplicationContext context: Context,
        @Named("INFURA_KEY") infuraKey: String
    ): Ethereum {
        return Ethereum(
            context = context,
            dappMetadata = DappMetadata(
                name = context.applicationInfo.name,
                url = "https://${context.applicationInfo.name}.com"
            ),
            sdkOptions = SDKOptions(infuraAPIKey = infuraKey)
        )
    }

    @Provides
    @Singleton
    fun provideEthereumRepository(
        ethereum: Ethereum,
        @Named("CONTRACT_ADDRESS") contractAddress: String,
        web3j: Web3j,
        gasProvider: StaticGasProvider
    ): EthereumRepository {
        return EthereumRepositoryImpl(ethereum, contractAddress, web3j, gasProvider)
    }

    @Provides
    @Singleton
    fun providePinataService(
        client: OkHttpClient,
        @Named("PINATA_JWT") jwtToken: String
    ): PinataService {
        return PinataServiceImpl(client, jwtToken)
    }

    @Provides
    @Singleton
    fun provideAssetParser(
        client: OkHttpClient
    ): AssetParser {
        return AssetParserImpl(client)
    }

    @Provides
    fun provideAssetUseCases(
        getAssets: GetAssetsUseCase,
        placeAssetBid: PlaceBidUseCase,
        executeAssetBuyout: BuyoutUseCase,
        createAndUploadAsset: CreateAssetUseCase,
        listAssetForAuction: ListAssetUseCase,
        finalizeAuction: FinalizeAuctionUseCase,
    ): AssetUseCases = AssetUseCases(
        getAssets, placeAssetBid, executeAssetBuyout, createAndUploadAsset, listAssetForAuction, finalizeAuction
    )

    @Provides
    fun provideWalletUseCases(
        connect: ConnectWalletUseCase,
        proceedWithoutWallet: ConnectWithoutWalletUseCase,
        fetchWalletBalance: GetBalanceUseCase,
        performLogout: LogoutUseCase
    ): WalletUseCases = WalletUseCases(
        connect, proceedWithoutWallet, fetchWalletBalance, performLogout
    )

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient()

    @Provides
    @Singleton
    fun provideWeb3j(@Named("INFURA_KEY") infuraKey: String): Web3j {
        return Web3j.build(HttpService("https://sepolia.infura.io/v3/$infuraKey"))
    }

    @Provides
    @Singleton
    fun provideGasProvider(): StaticGasProvider {
        return StaticGasProvider(BigInteger.ZERO, BigInteger.valueOf(16000000))
    }

    @Provides
    @Named("CONTRACT_ADDRESS")
    fun provideContractAddress(): String = "0xB99FCd3e9484CFc87c81bdaaa1d051fD3e990665"

    @Provides
    @Named("INFURA_KEY")
    fun provideInfuraKey(): String = "168f1531e44d4fd5bba521389b58daa1"

    @Provides
    @Named("PINATA_JWT")
    fun providePinataJwt(): String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySW5mb3JtYXRpb24iOnsiaWQiOiJjNmExY2QxOC0xNWI3LTQxYzEtODgyYS0xOTgwYjNlNjQxMTUiLCJlbWFpbCI6Im1hdHVzeWFrLm1ha3N5bUBsbGwua3BpLnVhIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInBpbl9wb2xpY3kiOnsicmVnaW9ucyI6W3siZGVzaXJlZFJlcGxpY2F0aW9uQ291bnQiOjEsImlkIjoiRlJBMSJ9LHsiZGVzaXJlZFJlcGxpY2F0aW9uQ291bnQiOjEsImlkIjoiTllDMSJ9XSwidmVyc2lvbiI6MX0sIm1mYV9lbmFibGVkIjpmYWxzZSwic3RhdHVzIjoiQUNUSVZFIn0sImF1dGhlbnRpY2F0aW9uVHlwZSI6InNjb3BlZEtleSIsInNjb3BlZEtleUtleSI6IjAyZGFiNWM1NDhjMDNiNzUxNmIzIiwic2NvcGVkS2V5U2VjcmV0IjoiMGRmMzZkMTJkNGNiODRhNTBkYzkyYTk2NTA5ODE0MmM3MzAzOTczODlkOTFjNTE3MDgwYjhiZTM5NWM5ZTcwZSIsImV4cCI6MTc3ODQxODgwNH0.Lc_giNfGb3fMtAftABCA7_6BY9dIquYGCNhoqxaPLq0"
}