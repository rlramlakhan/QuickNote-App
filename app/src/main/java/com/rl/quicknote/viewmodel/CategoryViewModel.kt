package com.rl.quicknote.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rl.quicknote.model.entities.Category
import com.rl.quicknote.model.repositories.CategoryRepository

class CategoryViewModel(private val categoryRepository: CategoryRepository): ViewModel() {

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    fun getCategories() {
        categoryRepository.getCategories().observeForever { categoriesList ->
            val list = mutableListOf<Category>()

            list.add(Category("defaultAll", "All", "local"))
            list.add(Category("defaultFavorite", "Favorites", "local"))
            list.addAll(categoriesList)
            list.add(Category("defaultAdd", "+", "local"))
            _categories.value = list
        }

    }

    fun addCategory(category: Category) {
        categoryRepository.addCategory(category)
    }

    fun deleteCategory(category: Category) {
        categoryRepository.deleteCategory(category)
    }
}

class CategoryViewModelFactory(private val categoryRepository: CategoryRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoryViewModel(categoryRepository) as T
    }
}