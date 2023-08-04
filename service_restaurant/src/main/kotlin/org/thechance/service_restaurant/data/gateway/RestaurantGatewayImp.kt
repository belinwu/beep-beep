package org.thechance.service_restaurant.data.gateway

import com.mongodb.client.model.Updates
import org.bson.types.ObjectId
import org.koin.core.annotation.Single
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.aggregate
import org.litote.kmongo.id.WrappedObjectId
import org.thechance.service_restaurant.data.DataBaseContainer
import org.thechance.service_restaurant.data.collection.*
import org.thechance.service_restaurant.data.collection.mapper.toCollection
import org.thechance.service_restaurant.data.collection.mapper.toEntity
import org.thechance.service_restaurant.data.utils.isSuccessfullyUpdated
import org.thechance.service_restaurant.data.utils.paginate
import org.thechance.service_restaurant.data.utils.toObjectIds
import org.thechance.service_restaurant.entity.Address
import org.thechance.service_restaurant.entity.Category
import org.thechance.service_restaurant.entity.Meal
import org.thechance.service_restaurant.entity.Restaurant
import org.thechance.service_restaurant.utils.Constants.ADDRESS_COLLECTION
import org.thechance.service_restaurant.utils.Constants.CATEGORY_COLLECTION
import org.thechance.service_restaurant.utils.Constants.MEAL_COLLECTION

@Single
class RestaurantGatewayImp(private val container: DataBaseContainer) : RestaurantGateway {
    //region Restaurant
    override suspend fun getRestaurants(page: Int, limit: Int): List<Restaurant> {
        return container.restaurantCollection.find(RestaurantCollection::isDeleted eq false)
            .paginate(page, limit).toList().toEntity()
    }

    //need to change
    override suspend fun getRestaurant(id: String): Restaurant? {
        return container.restaurantCollection.aggregate<RestaurantCollection>(
            match(and(RestaurantCollection::id eq ObjectId(id), RestaurantCollection::isDeleted eq false)),
        ).toList().firstOrNull()?.toEntity(getAddressesInRestaurant(id))
    }

    override suspend fun getCategoriesInRestaurant(restaurantId: String): List<Category> {
        return container.restaurantCollection.aggregate<CategoryRestaurant>(
            match(RestaurantCollection::id eq ObjectId(restaurantId)),
            lookup(
                from = CATEGORY_COLLECTION,
                resultProperty = CategoryRestaurant::categories,
                pipeline = listOf(match(CategoryCollection::isDeleted eq false)).toTypedArray()
            ),
        ).toList().first().categories.toEntity()
    }

    override suspend fun getMealsInRestaurant(restaurantId: String): List<Meal> {
       return container.restaurantCollection.aggregate<RestaurantMeal>(
           match(RestaurantCollection::id eq ObjectId(restaurantId)),
           lookup(
               from = MEAL_COLLECTION,
               resultProperty = RestaurantMeal::meals,
               pipeline = listOf(match(MealCollection::isDeleted eq false)).toTypedArray()
           ),
       ).toList().first().meals.toEntity()
    }

    override suspend fun addRestaurant(restaurant: Restaurant): Boolean {
        return container.restaurantCollection.insertOne(restaurant.toCollection()).wasAcknowledged()
    }

    override suspend fun addCategoriesToRestaurant(restaurantId: String, categoryIds: List<String>): Boolean {
        val resultAddToCategory = container.categoryCollection.updateMany(
            CategoryCollection::id `in` categoryIds.toObjectIds(),
            addToSet(CategoryCollection::restaurantIds, ObjectId(restaurantId))
        ).isSuccessfullyUpdated()

        val resultAddToRestaurant = container.restaurantCollection.updateOneById(
            ObjectId(restaurantId),
            update = Updates.addEachToSet(RestaurantCollection::categoryIds.name, categoryIds.toObjectIds())
        ).isSuccessfullyUpdated()

        return resultAddToCategory and resultAddToRestaurant
    }

    override suspend fun addMealsToRestaurant(restaurantId: String, mealIds: List<String>): Boolean {
        return container.restaurantCollection.updateOneById(
            ObjectId(restaurantId),
            update = Updates.addEachToSet(RestaurantCollection::mealIds.name, mealIds.toObjectIds())
        ).isSuccessfullyUpdated()
    }

    override suspend fun updateRestaurant(restaurant: Restaurant): Boolean {
        return container.restaurantCollection.updateOneById(
            id = ObjectId(restaurant.id),
            update = restaurant.toCollection(),
            updateOnlyNotNullProperties = true
        ).isSuccessfullyUpdated()
    }

    override suspend fun deleteRestaurant(restaurantId: String): Boolean {
        return container.restaurantCollection.updateOneById(
            id = ObjectId(restaurantId),
            update = Updates.set(RestaurantCollection::isDeleted.name, true),
        ).isSuccessfullyUpdated()
    }

    override suspend fun deleteCategoriesInRestaurant(restaurantId: String, categoryIds: List<String>): Boolean {
        val resultDeleteFromCategory = container.categoryCollection.updateMany(
            CategoryCollection::id `in` categoryIds.toObjectIds(),
            pull(CategoryCollection::restaurantIds, ObjectId(restaurantId))
        ).isSuccessfullyUpdated()

        val resultDeleteFromRestaurant = container.restaurantCollection.updateOneById(
            ObjectId(restaurantId),
            pullAll(RestaurantCollection::categoryIds, categoryIds.toObjectIds())
        ).isSuccessfullyUpdated()
        return resultDeleteFromRestaurant and resultDeleteFromCategory
    }

    override suspend fun deleteMealsInRestaurant(restaurantId: String, mealIds: List<String>): Boolean {
       return container.restaurantCollection.updateOneById(
            ObjectId(restaurantId),
            pullAll(RestaurantCollection::mealIds, mealIds.toObjectIds())
        ).isSuccessfullyUpdated()
    }
    //endregion

    //region addresses
    override suspend fun addAddressesToRestaurant(
        restaurantId: String,
        addressesIds: List<String>
    ): Boolean {
        val addresses = container.addressCollection.find(
            and(
                AddressCollection::id `in` addressesIds.map { ObjectId(it) },
                AddressCollection::isDeleted ne true
            )
        ).toList()

        return container.restaurantCollection.updateOneById(
            ObjectId(restaurantId),
            update = Updates.addEachToSet(
                RestaurantCollection::addressIds.name,
                addresses.map { it.id }
            )
        ).isSuccessfullyUpdated()
    }

    override suspend fun getAddressesInRestaurant(restaurantId: String): List<Address> {
        return container.restaurantCollection.aggregate<RestaurantAddressesCollection>(
            match(
                and(
                    RestaurantCollection::id eq ObjectId(restaurantId),
                    RestaurantCollection::isDeleted ne true
                )
            ),
            lookup(
                from = ADDRESS_COLLECTION,
                resultProperty = RestaurantAddressesCollection::addresses,
                pipeline = arrayOf(match(AddressCollection::isDeleted ne true))
            )
        ).toList().firstOrNull()?.addresses?.toEntity() ?: emptyList()
    }

    override suspend fun deleteAddressesInRestaurant(
        restaurantId: String,
        addressesIds: List<String>
    ): Boolean {
        return container.restaurantCollection.updateOneById(
            ObjectId(restaurantId),
            pullAll(RestaurantCollection::addressIds, addressesIds.map { WrappedObjectId(it) })
        ).isSuccessfullyUpdated()
    }
    //endregion
}