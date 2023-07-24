import 'package:flutter/cupertino.dart';
import 'package:ui/ribbit_app.dart';

Future main() async {
  final app = await buildApp(
      ribbitHost: Uri.parse('https://ribbit-api.andrewohara.com'),
      auth0Domain: 'ribbit.us.auth0.com',
      auth0ClientId: 'CHqh6abyoHVW1dbNcp0A5sXHU2jMQk09'
  );
  runApp(app);
}