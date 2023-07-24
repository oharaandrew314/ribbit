import 'package:flutter/material.dart';

import '../controllers/dtos.dart';

// TODO convert to dropdown_search with prompt
class SubSelector extends StatelessWidget {
  final List<SubDtoV1> subs;
  final SubDtoV1? selected;
  final Function(SubDtoV1)? select;

  const SubSelector({
    required this.subs,
    required this.selected,
    required this.select,
    Key? key
  }): super(key: key);

  void onChanged(SubDtoV1? sub) {
    if (sub != null) select?.call(sub);
  }

  @override
  Widget build(BuildContext context) {
    final items = subs.map((sub) => DropdownMenuItem(
        value: sub,
        child: Text("/r/${sub.id}"),
    )).toList();

    return DropdownButton<SubDtoV1>(
        value: selected,
        items: items,
        onChanged: onChanged,
    );
  }

}