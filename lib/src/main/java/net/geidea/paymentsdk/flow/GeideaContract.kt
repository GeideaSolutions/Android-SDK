package net.geidea.paymentsdk.flow

import androidx.activity.result.contract.ActivityResultContract

public abstract class GeideaContract<T, R> : ActivityResultContract<T, R>() {

    companion object {
        /**
         * [Int] theme id (optional) to apply customized theme.
         * The customized theme must extend one of:
         *
         * Material 2:
         * - [net.geidea.paymentsdk.R.style.Gd_Theme_DayNight_NoActionBar] (default)
         * - [net.geidea.paymentsdk.R.style.Gd_Base_Theme_DayNight_NoActionBar]
         * - [net.geidea.paymentsdk.R.style.Gd_Base_Theme_NoActionBar]
         * - [net.geidea.paymentsdk.R.style.Gd_Base_Theme_Light_NoActionBar]
         *
         * Material 3 (beta):
         * - [net.geidea.paymentsdk.R.style.Gd_Theme_Material3_DayNight_NoActionBar]
         * - [net.geidea.paymentsdk.R.style.Gd_Base_Theme_Material3_DayNight_NoActionBar]
         * - [net.geidea.paymentsdk.R.style.Gd_Base_Theme_Material3_Light_NoActionBar]
         * - [net.geidea.paymentsdk.R.style.Gd_Base_Theme_Material3_DynamicColors_DayNight]
         *
         * See Integration guide for more information.
         */
        public const val PARAM_THEME = "net.geidea.paymentsdk.flow.GeideaContract.PARAM_THEME"
    }
}