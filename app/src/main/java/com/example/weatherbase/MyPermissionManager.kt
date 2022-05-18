package com.example.weatherbase

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MyPermissionManager {

    fun withPermission(
        activity: ComponentActivity,
        permission: String,
        onPermission: () -> Unit,
        onProhibited: (() -> Unit)? = null,
    ): Unit {
        val alreadyGranted = isPermissionGranted(permission, activity)

        if (alreadyGranted) {
            onPermission()
        } else {
            setActionWithPermissionRequest(activity, permission, onPermission, onProhibited)
        }
    }

    private fun isPermissionGranted(permission: String, context: Context): Boolean {
        val state = ContextCompat.checkSelfPermission(context, permission)
        return state == PackageManager.PERMISSION_GRANTED
    }

    private fun setActionWithPermissionRequest(
        activity: ComponentActivity,
        permission: String,
        onPermission: () -> Unit,
        onProhibited: (() -> Unit)?,
    ) {
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.getOrDefault(permission, false)) {
                onPermission()
            } else {
                onProhibited?.invoke()
            }
        }.launch(arrayOf(permission))
    }
}
