package com.gyvacha.androidssh.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.gyvacha.androidssh.domain.model.navigation.AppNavigation
import com.gyvacha.androidssh.domain.model.navigation.BottomNavItem

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        val items = BottomNavItem.getItemsList()

        items.forEach { (item, route) ->
            val itemLabel = stringResource(id = item.label)
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any {
                    it.hasRoute(route::class)
                } == true,
                label = { Text(itemLabel) },
                icon = { Icon(imageVector = item.icon, contentDescription = itemLabel) },
                onClick = {
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(AppNavigation.HostsRoute.Hosts) {
                            saveState = true
                            inclusive = false
                        }
                    }
                }
            )
        }
    }
}