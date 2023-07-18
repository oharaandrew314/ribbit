import 'package:flutter/material.dart';
import 'package:logger/logger.dart';

final _log = Logger();

Widget userLink(String username) {
  return InkWell(
      child: Text("/u/$username"),
      onTap: () => {
        _log.i('Go to $username')
      }
  );
}