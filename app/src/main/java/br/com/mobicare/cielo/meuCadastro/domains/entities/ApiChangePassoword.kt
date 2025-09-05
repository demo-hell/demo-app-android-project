//TODO corrigir package
package br.com.mobicare.cielo.meusCartoes.clients.api.domain


data class BodyChangePassword(val currentPassword:String, val newPassword:String, val newPasswordConfirmation:String)
