import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const EditTodoPage(),
    );
  }
}

class EditTodoPage extends StatefulWidget {
  const EditTodoPage({Key? key}) : super(key: key);

  @override
  State<EditTodoPage> createState() => _EditTodoPageState();
}

class _EditTodoPageState extends State<EditTodoPage> {
  final _controller = TextEditingController();

  final _channel = const MethodChannel('edit_todo_channel');

  final _validator = ValueNotifier<bool>(false);

  final _initialValueCompleter = ValueNotifier<String?>(null);

  @override
  void initState() {
    _controller.addListener(() {
      _validator.value = _controller.text.trim().isNotEmpty;
    });
    _channel.setMethodCallHandler((call) async {
      switch (call.method) {
        case 'setInitialValue':
          _initialValueCompleter.value = null;
          final value = call.arguments.toString().trim();
          _initialValueCompleter.value = value;
          _validator.value = value.isNotEmpty;
          _controller.text = value;
        case 'attemptToSave':
          _channel.invokeMethod('editTodo', _controller.text.trim());
      }
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: ValueListenableBuilder<String?>(
          valueListenable: _initialValueCompleter,
          builder: (context, value, __) {
            if (value == null) {
              return const Center(
                child: CircularProgressIndicator(),
              );
            }

            return GestureDetector(
              behavior: HitTestBehavior.opaque,
              onTap: () {
                FocusScope.of(context).unfocus();
              },
              child: Center(
                child: Padding(
                    padding: const EdgeInsets.all(32.0),
                    child: TextField(
                      controller: _controller,
                      decoration: const InputDecoration(
                        labelText: 'Edit your todo',
                        border: OutlineInputBorder(),
                      ),
                    )),
              ),
            );
          }),
      floatingActionButton: ValueListenableBuilder<bool>(
        valueListenable: _validator,
        builder: (_, valid, __) => FloatingActionButton(
          backgroundColor: valid ? null : Colors.grey,
          onPressed: valid
              ? () {
                  _channel.invokeMethod<bool>('editTodo', _controller.text.trim());
                }
              : null,
          child: const Icon(Icons.save),
        ),
      ),
    );
  }
}
