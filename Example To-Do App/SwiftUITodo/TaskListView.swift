//
//  TaskListView.swift
//  SwiftUITodo
//
//  Created by Suyeol Jeon on 03/06/2019.
//  Copyright © 2019 Suyeol Jeon. All rights reserved.
//

import SwiftUI
import Flutter


struct TaskListView: View {
    @EnvironmentObject var userData: UserData
    @EnvironmentObject var flutterDependencies: FlutterDependencies
    @State var draftTitle: String = ""
    @State var isEditing: Bool = false
    
    var body: some View {
        NavigationView {
            List {
                Button(action: { showFlutter() }) {
                  Text("Create a New task")
                }
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
        }
    }
    
    private func createTask(title: String) {
        let newTask = Task(title: title, isDone: false)
        self.userData.tasks.insert(newTask, at: 0)
        self.draftTitle = ""
    }
    
    func showFlutter() {
        // Get the RootViewController.
        guard
            let windowScene = UIApplication.shared.connectedScenes
                .first(where: { $0.activationState == .foregroundActive && $0 is UIWindowScene }) as? UIWindowScene,
            let window = windowScene.windows.first(where: \.isKeyWindow),
            let rootViewController = window.rootViewController
        else { return }
        
        
        let flutterViewController = FlutterViewController(
            engine: flutterDependencies.addTodoFlutterEngine,
            nibName: nil,
            bundle: nil)
        flutterViewController.modalPresentationStyle = .pageSheet
        flutterViewController.isViewOpaque = false
        
        rootViewController.present(flutterViewController, animated: true)
        
        let channel = FlutterMethodChannel(
            name: "add_todo_channel",
            binaryMessenger: flutterViewController.binaryMessenger
        )
        channel.setMethodCallHandler(
            {
                (call: FlutterMethodCall, result: FlutterResult) -> Void in
                switch (call.method) {
                case "addTodo":
                    createTask(title: call.arguments as! String)
                    rootViewController.dismiss(animated: true)
                    result(true)
                default:
                    result(FlutterMethodNotImplemented)
                }
            }
        )
    }
}

