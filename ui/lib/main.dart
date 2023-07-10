import 'package:auth0_flutter/auth0_flutter.dart';
import 'package:auth0_flutter/auth0_flutter_web.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:ui/MainView.dart';

Future main() async{
  await dotenv.load(fileName: ".env");
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    final auth0Web = kIsWeb ? Auth0Web(dotenv.env['AUTH0_DOMAIN']!, dotenv.env['AUTH0_CLIENT_ID_WEB']!) : null;
    final auth0Native = kIsWeb ? null : Auth0(dotenv.env['AUTH0_DOMAIN']!, dotenv.env['AUTH0_CLIENT_ID_NATIVE']!);
    final auth0NativeScheme = kIsWeb ? null : dotenv.env['AUTH0_CUSTOM_SCHEME_NATIVE'];
    final redirectUrlWeb = kIsWeb ? dotenv.env['AUTH0_REDIRECT_URL_WEB'] : null;

    return MaterialApp(
      title: 'Ribbit',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: MainView(
          auth0Web: auth0Web,
          auth0Native: auth0Native,
          auth0NativeScheme: auth0NativeScheme,
          redirectUrlWeb: redirectUrlWeb,
      ),
    );
  }
}
