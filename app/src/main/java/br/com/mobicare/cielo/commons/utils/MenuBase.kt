package br.com.mobicare.cielo.commons.utils

import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.main.domain.Menu

open class MenuBase(private val featureTogglePreference: FeatureTogglePreference) {
    fun processMenuResult(servicesMenu: List<Menu>, onProcessed: (List<Menu>) -> Unit) {
        val removeMenus = generateListRemoveMenu(servicesMenu.first().items)

        val menu = servicesMenu.first().items?.filter {
            removeMenus.contains("MENU_"+it.code).not()
        } ?: servicesMenu

        servicesMenu.first().items = menu
        onProcessed(servicesMenu)
    }

    /**
     * Generates a list of menu items to be hidden.
     *
     * This function first creates a map from the provided list of menu items adding MENU_ prefix to each code, where the key is the `code` of each `Menu`.
     * It then retrieves all feature toggles and filters out those that are not present in the menu map or are set to be shown.
     * Finally, it maps the remaining feature toggles to their feature names, ignoring any null values.
     *
     * The purpose of this function is to control the visibility of the menu without adding new code. If it's necessary to hide a menu, simply create a feature toggle with the same name as the menu code. If there's no feature toggle created with the same name, the feature toggle will be ignored and the menu will follow its own show status.
     *
     * For example: If the menu returns "APP_ANDROID_RECEBA_MAIS", and we have a feature toggle called "MENU_APP_ANDROID_RECEBA_MAIS", the value of the feature toggle will be considered, not the menu's show property.
     * If there's no feature toggle with this name, the value of the menu's show property will be considered.
     *
     * @param menuItems The list of menu items to be processed. If this is null, an empty map is used.
     * @return A list of feature names to be removed from the menu. If no feature names are to be removed, an empty list is returned.
     */
    private fun generateListRemoveMenu(menuItems: List<Menu>?): List<String> {
        val menuItemsMap = menuItems?.associateBy { "MENU_"+it.code } ?: emptyMap()

        return featureTogglePreference.getAllFeatureToggles()
            .filter { ft ->
                ft.featureName in menuItemsMap && ft.show?.not() ?: false
            }
            .mapNotNull { it.featureName }
    }
}