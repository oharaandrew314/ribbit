import 'package:flutter/material.dart';
import 'package:logger/logger.dart';

final _log = Logger();

Widget userLink(String subId) {
  return InkWell(
      child: Text("/r/$subId"),
      onTap: () => {
        _log.i('Go to $subId')
      }
  );
}