import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

const _channel = MethodChannel('add_todo_channel');

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
      routes: {
        '/': (_) => const AddTodoPage(),
        '/for_activity': (_) => const _ForActivity(),
      },
    );
  }
}

class AddTodoPage extends StatefulWidget {
  const AddTodoPage({Key? key}) : super(key: key);

  @override
  State<AddTodoPage> createState() => _AddTodoPageState();
}

class _AddTodoPageState extends State<AddTodoPage> {
  final _controller = TextEditingController();

  final _validator = ValueNotifier<bool>(false);

  @override
  void initState() {
    _controller.addListener(() {
      _validator.value = _controller.text.trim().isNotEmpty;
    });
    _channel.setMethodCallHandler((call) => switch (call.method) {
          'tryToAddTodo' => _tryToAdd(makePop: false),
          _ => throw MissingPluginException(call.method)
        });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: GestureDetector(
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
                labelText: 'Enter your todo',
                border: OutlineInputBorder(),
              ),
            ),
          ),
        ),
      ),
      floatingActionButton: ValueListenableBuilder<bool>(
        valueListenable: _validator,
        builder: (_, valid, __) => FloatingActionButton(
          backgroundColor: valid ? null : Colors.grey,
          onPressed: valid ? () => _tryToAdd() : null,
          child: const Icon(Icons.add),
        ),
      ),
    );
  }

  Future<void> _tryToAdd({bool makePop = true}) async {
    var success = false;
    try {
      success = await _channel.invokeMethod<bool>(
              'addTodo', _controller.text.trim()) ??
          success;
      if (success) _controller.clear();
    } catch (e, s) {
      log(e.toString());
      log(s.toString());
    }
  }
}

class _ForActivity extends StatefulWidget {
  const _ForActivity({Key? key}) : super(key: key);

  @override
  State<_ForActivity> createState() => _ForActivityState();
}

class _ForActivityState extends State<_ForActivity> {
  final _channel = const MethodChannel('example_activity_channel');

  num? batteryLvl;

  @override
  void initState() {
    _channel.setMethodCallHandler(
      (call) async {
        switch (call.method) {
          case 'sendBatteryLevel':
            final arg = call.arguments;
            if (arg is num) setState(() => batteryLvl = arg);
          default:
            throw MissingPluginException();
        }
      },
    );
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Screen for activity'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back_ios),
          onPressed: () => SystemNavigator.pop(),
        ),
      ),
      body: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              'current battery level is ${batteryLvl == null ? 'unknown' : batteryLvl.toString()}',
            ),
            const SizedBox(height: 20),
            OutlinedButton(
              onPressed: () async {
                final result = await showDialog<String>(
                  context: context,
                  builder: (context) {
                    final controller = TextEditingController();
                    return AlertDialog(
                      title: const Text('Enter text for snack'),
                      content: TextField(
                        controller: controller,
                      ),
                      actions: [
                        TextButton(
                          onPressed: () {
                            Navigator.pop(context, controller.text);
                          },
                          child: const Text('Submit'),
                        ),
                      ],
                    );
                  },
                );
                if (result != null) _channel.invokeMethod('showSnack', result);
              },
              child: const Text('Show android snack'),
            ),
            const SizedBox(height: 20),
            OutlinedButton(
              onPressed: () {
                _channel.invokeMethod('getBatteryLevel');
              },
              child: const Text('Check battery lvl'),
            ),
          ],
        ),
      ),
    );
  }
}
