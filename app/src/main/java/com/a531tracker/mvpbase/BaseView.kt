package com.a531tracker.mvpbase

interface BaseView<T> {
    fun setPresenter(presenter: T)
}