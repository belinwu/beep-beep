package org.thechance.service_identity.data.geteway

import com.mongodb.client.model.Filters
import org.bson.types.ObjectId
import org.koin.core.annotation.Single
import org.litote.kmongo.eq
import org.litote.kmongo.set
import org.litote.kmongo.setTo
import org.litote.kmongo.setValue
import org.thechance.service_identity.data.DataBaseContainer
import org.thechance.service_identity.data.collection.*
import org.thechance.service_identity.data.mappers.toCollection
import org.thechance.service_identity.data.mappers.toEntity
import org.thechance.service_identity.data.util.USER_DETAILS_COLLECTION
import org.thechance.service_identity.data.util.isUpdatedSuccessfully
import org.thechance.service_identity.domain.entity.Permission
import org.thechance.service_identity.domain.entity.Wallet
import org.thechance.service_identity.domain.gateway.*

@Single
class DataBaseGatewayImp(dataBaseContainer: DataBaseContainer) : DataBaseGateway {
    private val addressCollection by lazy {
        dataBaseContainer.database.getCollection<AddressCollection>(ADDRESS_COLLECTION_NAME)
    }

    private val userDetailsCollection by lazy {
        dataBaseContainer.database.getCollection<UserDetailsCollection>(USER_DETAILS_COLLECTION)
    }
    private val permissionCollection by lazy {
        dataBaseContainer.database.getCollection<PermissionCollection>(
            PERMISSION_COLLECTION_NAME
        )
    }
    private val userCollection by lazy {
        dataBaseContainer.database.getCollection<UserCollection>(
            USER_COLLECTION
        )
    }
    private val walletCollection by lazy { dataBaseContainer.database.getCollection<WalletCollection>(WALLET_COLLECTION) }

    //region Address

    //endregion


    //region userDetails

    //endregion


    //region Permission
    override suspend fun getPermission(permissionId: String): Permission? {
        return permissionCollection.findOneById(ObjectId(permissionId))?.toEntity()
            ?: throw Exception("Wallet not found")
    }

    override suspend fun addPermission(permission: Permission): Boolean {
        return permissionCollection.insertOne(permission.toCollection()).wasAcknowledged()

    }

    override suspend fun deletePermission(permissionId: String): Boolean {
        return permissionCollection.updateOne(
            filter = Filters.and(
                PermissionCollection::id eq ObjectId(permissionId),
                PermissionCollection::isDeleted eq false
            ),
            update = setValue(PermissionCollection::isDeleted, true)
        ).isUpdatedSuccessfully()
    }



    override suspend fun updatePermission(permissionId: String, permission: Permission): Boolean {
        return permissionCollection.updateOneById(
            id = ObjectId(permissionId),
            update = permission.toCollection(),
        ).wasAcknowledged()
    }
    //endregion


    //region User

    //endregion


    companion object {
        private const val WALLET_COLLECTION = "wallet"
        private const val ADDRESS_COLLECTION_NAME = "address"
        private const val PERMISSION_COLLECTION_NAME = "permission"
        private const val USER_COLLECTION = "user"
        const val CLIENT_PERMISSION = 1
        private const val ADMIN_PERMISSION = 2
        private const val DELIVERY_PERMISSION = 3
        private const val TAXI_DRIVER_PERMISSION = 4
        private const val RESTAURANT_OWNER_PERMISSION = 5
        private const val SUPPORT_PERMISSION = 6
    }
    // region: wallet
    override suspend fun getWallet(walletId: String): Wallet {
        return walletCollection.findOneById(ObjectId(walletId))?.toEntity() ?: throw Exception("Wallet not found")
    }

    override suspend fun createWallet(wallet: Wallet): Boolean {
        userDetailsCollection.updateOne(
            filter = UserDetailsCollection::userId eq ObjectId(wallet.userId),
            update = set(UserDetailsCollection::walletId setTo wallet.id)
        )
        return walletCollection.insertOne(wallet.toCollection()).wasAcknowledged()
    }

    override suspend fun updateWallet(walletId: String, wallet: Wallet): Boolean {
        return walletCollection.updateOneById(
            id = ObjectId(walletId),
            update = wallet.toCollection(),
        ).wasAcknowledged()
    }
    // endregion: wallet

}