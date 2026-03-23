package goobaserver.di

import goobaserver.model.User
import goobaserver.db.UserService
import goobaserver.db.UserServiceImpl
import org.koin.dsl.module

/** Every time the app asks for a UserService interface
 * The given implementation is handed over
 */
val appModule = module {
    single<UserService> {
        UserServiceImpl()
    }
}