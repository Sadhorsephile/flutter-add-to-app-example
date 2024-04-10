//
//  TaskItemView.swift
//  SwiftUITodo
//
//  Created by Suyeol Jeon on 03/06/2019.
//  Copyright © 2019 Suyeol Jeon. All rights reserved.
//

import SwiftUI
import Flutter

struct TaskItemView: View {
    @EnvironmentObject var userData: UserData
    @EnvironmentObject var flutterDependencies: FlutterDependencies
    
    let task: Task
    @Binding var isEditing: Bool
    
    var body: some View {
        return HStack {
            if self.isEditing {
                Image(systemName: "minus.circle")
                    .foregroundColor(.red)
                    .onTapGesture(count: 1) {
                        self.delete()
                    }
                Button(action: {
                    showFlutter(task: task)
                }) {
                    Text(task.title)
                }
            } else {
                Button(action: { self.toggleDone() }) {
                    Text(self.task.title)
                }
                Spacer()
                if task.isDone {
                    Image(systemName: "checkmark").foregroundColor(.green)
                }
            }
        }
    }
    
    func showFlutter(task: Task) {
        // Get the RootViewController.
        guard
            let windowScene = UIApplication.shared.connectedScenes
                .first(where: { $0.activationState == .foregroundActive && $0 is UIWindowScene }) as? UIWindowScene,
            let window = windowScene.windows.first(where: \.isKeyWindow),
            let rootViewController = window.rootViewController
        else { return }
        
        
        let flutterViewController = FlutterViewController(
            engine: flutterDependencies.editTodoFlutterEngine,
            nibName: nil,
            bundle: nil)
        flutterViewController.modalPresentationStyle = .pageSheet
        flutterViewController.isViewOpaque = false
        
        rootViewController.present(flutterViewController, animated: true)
        
        let channel = FlutterMethodChannel(
            name: "edit_todo_channel",
            binaryMessenger: flutterViewController.binaryMessenger
        )
        
        channel.invokeMethod("setInitialValue", arguments: task.title)
        channel.setMethodCallHandler(
            {
                (call: FlutterMethodCall, result: FlutterResult) -> Void in
                switch (call.method) {
                case "editTodo":
                    updateTask(task: task, title: call.arguments as! String)
                    rootViewController.dismiss(animated: true)
                    result(true)
                default:
                    result(FlutterMethodNotImplemented)
                }
            }
        )
    }
    
    private func updateTask(task: Task, title: String) {
        guard let index = self.userData.tasks.firstIndex(of: task) else { return }
        self.userData.tasks[index].title = title
    }
    
    private func toggleDone() {
        guard !self.isEditing else { return }
        guard let index = self.userData.tasks.firstIndex(where: { $0.id == self.task.id }) else { return }
        self.userData.tasks[index].isDone.toggle()
    }
    
    private func delete() {
        self.userData.tasks.removeAll(where: { $0.id == self.task.id })
        if self.userData.tasks.isEmpty {
            self.isEditing = false
        }
    }
}
