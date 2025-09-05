package br.com.mobicare.cielo.accessManager.utils

sealed class AccessManagerBatchChangeProfileUiState {
    class AssignRoleSuccess(): AccessManagerBatchChangeProfileUiState()
    class AssignRoleError(): AccessManagerBatchChangeProfileUiState()
}