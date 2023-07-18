import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:go_router/go_router.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:ui/controllers/login_provider.dart';
import 'package:ui/controllers/ribbit_client.dart';
import 'package:ui/screens/feed_screen.dart';

Future main() async {
  await dotenv.load(fileName: ".env");

  final client = RibbitClient(Uri.parse(dotenv.env["RIBBIT_HOST"]!));
  final prefs = await SharedPreferences.getInstance();

  final loginProvider = OAuthLoginProvider(
      dotenv.env['AUTH0_DOMAIN']!,
      dotenv.env['AUTH0_CLIENT_ID_WEB']!,
      prefs
  );

  final router = GoRouter(
      routes: [
        GoRoute(
            path: '/',
            builder: (context, state) => FeedScreen(client: client, provider: loginProvider),
            // routes: [
            //   GoRoute(
            //       path: 'foo',
            //       builder: (context, state) => Scaffold(
            //         appBar: AppBar(title: const Text('Foo')),
            //         body: const Text("HAI"),
            //       )
            //   )
            // ]
        )
      ]
  );

  final app = MaterialApp.router(
    title: 'Ribbit',
    routerConfig: router
  );

  runApp(app);
}