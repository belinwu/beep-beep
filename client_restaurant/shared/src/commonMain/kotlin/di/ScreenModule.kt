package di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import presentation.login.LoginScreenModel
import presentation.main.MainScreenModel
import presentation.info.RestaurantInfoScreenModel
import presentation.meals.MealsScreenModel

val screenModule = module {
    factoryOf(::LoginScreenModel)
    factoryOf(::MainScreenModel)
    factoryOf(::RestaurantInfoScreenModel)
    factoryOf(::MealsScreenModel)
}