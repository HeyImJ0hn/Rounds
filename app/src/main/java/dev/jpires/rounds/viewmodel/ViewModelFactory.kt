package dev.jpires.rounds.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import dev.jpires.rounds.model.repository.Repository

class ViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}