package org.d3if3138.mobpro1.asesmen3fida.system.navigation

sealed class Screen(val route: String) {
    data object Base : Screen("home")
}