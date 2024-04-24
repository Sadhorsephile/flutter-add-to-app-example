package com.example.todoapp

import android.app.Application
import io.flutter.FlutterInjector
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.FlutterEngineGroup
import io.flutter.embedding.engine.FlutterEngineGroupCache
import io.flutter.embedding.engine.dart.DartExecutor

class MainApplication : Application() {
    // Группа движков с общим скопом ресурсов.
    lateinit var engineGroup: FlutterEngineGroup

    // Id движков, которые мы будем использовать
    companion object Factory {
        const val addTodoNoduleEngineId = "add_todo_engine"
        const val exampleActivityNoduleEngineId = "example_activity_engine"
        const val editTodoNoduleEngineId = "edit_todo_engine"
        const val listTodosModuleEngineId = "list_todos_engine"
    }

    override fun onCreate() {
        super.onCreate()
        engineGroup = FlutterEngineGroup(this)
        val pathToBundle = FlutterInjector.instance().flutterLoader().findAppBundlePath()

        /// Запускаем наши движки

        val addTodoEngine = engineGroup.createAndRunEngine(
            this,
            DartExecutor.DartEntrypoint(
                pathToBundle,
                "startAddModule",
            ),
        );
        val editTodoEngine = engineGroup.createAndRunEngine(
            this,
            DartExecutor.DartEntrypoint(
                pathToBundle,
                "startEditModule",
            ),
        )
        val exampleActivityEngine = engineGroup.createAndRunEngine(
            this,
            DartExecutor.DartEntrypoint(
                pathToBundle,
                "startAddModule",

                ),
            "/for_activity"
        )

        val listTodosEngine = engineGroup.createAndRunEngine(
            this,
            DartExecutor.DartEntrypoint(
                pathToBundle,
                "startListModule",

                ),
        )

        /// И регистрируем их в кэше.

        FlutterEngineCache.getInstance().put(
            addTodoNoduleEngineId,
            addTodoEngine,
        )
        FlutterEngineCache.getInstance().put(
            editTodoNoduleEngineId,
            editTodoEngine,
        )
        FlutterEngineCache.getInstance().put(
            listTodosModuleEngineId,
            listTodosEngine,
        )
        FlutterEngineCache.getInstance().put(
            exampleActivityNoduleEngineId,
            exampleActivityEngine,
        )
    }
}