import 'package:add_todo_module/main.dart' as add;
import 'package:edit_todo_module/main.dart' as edit;
import 'package:list_todos_module/main.dart' as list;


void main() {}

@pragma('vm:entry-point')
void startAddModule(List<String> args) {
  add.main();
}

@pragma('vm:entry-point')
void startEditModule(List<String> args) {
  edit.main();
}

@pragma('vm:entry-point')
void startListModule(List<String> args) {
  list.main();
}
