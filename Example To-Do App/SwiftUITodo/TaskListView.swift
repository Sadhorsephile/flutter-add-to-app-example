//
//  TaskListView.swift
//  SwiftUITodo
//
//  Created by Suyeol Jeon on 03/06/2019.
//  Copyright © 2019 Suyeol Jeon. All rights reserved.
//

import SwiftUI
import Flutter


struct AddTodoBottomBlock: UIViewControllerRepresentable {
    
    var engine: FlutterEngine
    var createTask: ((String) -> Void)
    
    func makeUIViewController(context: Context) -> FlutterViewController {
        let controller = FlutterViewController(engine: engine, nibName: nil, bundle: nil)
        let channel = FlutterMethodChannel(
            name: "add_todo_channel",
            binaryMessenger: controller.binaryMessenger
        )
        channel.setMethodCallHandler(
            {
                (call: FlutterMethodCall, result: FlutterResult) -> Void in
                switch (call.method) {
                case "addTodo":
                    createTask(call.arguments as! String)
                    result(true)
                default:
                    result(FlutterMethodNotImplemented)
                }
            }
        )
        return controller
    }

    func updateUIViewController(_ uiViewController: FlutterViewController, context: Context) {
        // Место для обновления конфигурации, исходя из нового состояния приложения
    }
}

struct TaskListView: View {
    @EnvironmentObject var userData: UserData
    @EnvironmentObject var flutterDependencies: FlutterDependencies
    @State var draftTitle: String = ""
    @State var isEditing: Bool = false
    
    
    var body: some View {
        NavigationView {
            VStack{
                List {
                    ForEach(self.userData.tasks) { task in
                        TaskItemView(task: task, isEditing: self.$isEditing)
                    }
                }
                .navigationBarTitle(Text("Tasks 👀"))
                .navigationBarItems(trailing: Button(action: {
                    self.isEditing = !self.isEditing
                }) {
                    if !self.isEditing {
                        Text("Edit")
                    } else {
                        Text("Done").bold()
                    }
                })
                AddTodoBottomBlock(engine: flutterDependencies.addTodoFlutterEngine, createTask: createTask(title:)  ).frame(height: 300)
            }
            
        }
    }
    
    private func createTask(title: String) {
        let newTask = Task(title: title, isDone: false)
        self.userData.tasks.insert(newTask, at: 0)
        self.draftTitle = ""
    }
}

