import 'package:auth0_flutter/auth0_flutter.dart';
import 'package:auth0_flutter/auth0_flutter_web.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:ui/controllers/login_provider.dart';
import 'package:ui/screens/login_screen.dart';

Future main() async {
  await dotenv.load(fileName: ".env");

  final loginProvider = kIsWeb ?
      WebLoginProvider(
          auth0: Auth0Web(dotenv.env['AUTH0_DOMAIN']!, dotenv.env['AUTH0_CLIENT_ID_WEB']!),
          redirectUrl: dotenv.env['AUTH0_REDIRECT_URL_WEB']!
      )
      : NativeLoginProvider(
        auth0: Auth0(dotenv.env['AUTH0_DOMAIN']!, dotenv.env['AUTH0_CLIENT_ID_NATIVE']!),
        scheme: dotenv.env['AUTH0_CUSTOM_SCHEME_NATIVE']!
      );

  final loginScreen = LoginScreen(provider: loginProvider);

  runApp(loginScreen);
}

Widget ribbitApp(Widget child) {
  return MaterialApp(
      title: 'Ribbit',
      home: child
  );
}