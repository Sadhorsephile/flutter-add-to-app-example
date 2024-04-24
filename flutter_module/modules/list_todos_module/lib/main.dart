import 'dart:math';

import 'package:collection/collection.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: ListTodosWidget(),
    );
  }
}

class ListTodosWidget extends StatefulWidget {
  const ListTodosWidget({super.key});

  @override
  State<ListTodosWidget> createState() => _ListTodosWidgetState();
}

class _ListTodosWidgetState extends State<ListTodosWidget> {
  final todos = ValueNotifier<List<SimpleTodoDto>>([]);
  final channel = const MethodChannel('list_todos_module');
  final rand = Random();

  static const _colors = [
    Colors.red,
    Colors.green,
    Colors.blue,
    Colors.yellow,
    Colors.purple,
    Colors.orange,
    Colors.teal,
    Colors.pink,
    Colors.indigo,
    Colors.cyan,
  ];

  @override
  void dispose() {
    todos.dispose();
    super.dispose();
  }

  @override
  void initState() {
    channel.setMethodCallHandler(
      (call) async {
        if (call.method == 'sendList') {
          final rawTodos = call.arguments;
          if (rawTodos is! List) {
            throw Exception('Expected a List<String> but got ${rawTodos.runtimeType}');
          }

          todos.value = rawTodos
              .map((todo) {
                final parts = todo.toString().split(':');

                if (parts.length != 2) {
                  return null;
                }
                return SimpleTodoDto(id: parts[0], name: parts[1]);
              })
              .toList()
              .whereNotNull()
              .toList();
        }
      },
    );

    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return ValueListenableBuilder(
        valueListenable: todos,
        builder: (context, List<SimpleTodoDto> todos, _) {
          return GridView.builder(
            padding: const EdgeInsets.all(16),
            gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: 3,
              mainAxisSpacing: 8,
              crossAxisSpacing: 8,
              
            ),
            itemCount: todos.length,
            itemBuilder: (context, index) {
              final todo = todos[index];
              return Material(
                borderRadius: BorderRadius.circular(8),
                elevation: 4,
                color: Color.lerp(_colors[rand.nextInt(_colors.length)], Colors.white, 0.9),
                child: InkWell(
                  borderRadius: BorderRadius.circular(8),
                  onTap: () {
                    channel.invokeMethod('onPressed', todo.id);
                  },
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: FittedBox(child: Text(todo.name)),
                  ),
                ),
              );
            },
          );
        });
  }
}

class SimpleTodoDto {
  final String id;
  final String name;

  SimpleTodoDto({required this.id, required this.name});
}
