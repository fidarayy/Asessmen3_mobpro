package org.d3if3138.mobpro1.asesmen3fida.system.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.d3if3138.mobpro1.asesmen3fida.system.database.InstantGramViewModel
import org.d3if3138.mobpro1.asesmen3fida.system.database.model.User
import org.d3if3138.mobpro1.asesmen3fida.ui.screen.PublicPost

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier, instantModel: InstantGramViewModel, user: User) {
    NavHost(
        navController = navController,
        startDestination = Screen.Base.route
    ) {
        /*----------------[Main Route]----------------*/
        composable(route = Screen.Base.route) {
            /*PublicGrid("Home", viewModel = viewModel, postModel = instantModel, modifier = modifier, user)*/
            PublicPost(postModel = instantModel, modifier = modifier, user)
        }
    }
}