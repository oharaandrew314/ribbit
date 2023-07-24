import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:ui/controllers/login_provider.dart';
import 'package:ui/controllers/ribbit_client.dart';
import 'package:ui/screens/create_post_screen.dart';
import 'package:ui/screens/feed_screen.dart';
import 'package:ui/screens/make_profile_screen.dart';
import 'package:ui/screens/post_screen.dart';

Future<MaterialApp> buildApp({
  required Uri ribbitHost,
  required String auth0Domain,
  required String auth0ClientId,
}) async {
  final client = RibbitClient(ribbitHost);
  final prefs = await SharedPreferences.getInstance();
  final loginProvider = OAuthLoginProvider(auth0Domain, auth0ClientId, prefs);

  final router = GoRouter(
      routes: [
        GoRoute(
            path: '/',
            builder: (context, state) => FeedScreen(client: client, provider: loginProvider),
        ),
        GoRoute(
            path: '/login',
            builder: (context, state) => MakeProfileScreen(client: client, provider: loginProvider),
        ),
        GoRoute(
            path: '/new',
            builder: (context, state) => CreatePostScreen(client: client, provider: loginProvider)
        ),
        GoRoute(
          path: '/posts/:id',
          builder: (context, GoRouterState state) => PostScreen(
              client: client,
              principal: loginProvider.getUser(),
              postId: state.pathParameters['id']!
          )
        )
      ]
  );

  return MaterialApp.router(
    title: 'Ribbit',
    routerConfig: router
  );
}