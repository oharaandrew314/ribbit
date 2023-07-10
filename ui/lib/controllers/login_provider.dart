import 'package:auth0_flutter/auth0_flutter.dart';
import 'package:auth0_flutter/auth0_flutter_web.dart';

abstract class LoginProvider {
  Future<UserProfile?> silentLogin();
  Future<UserProfile?> login();
  Future logout();
}

class WebLoginProvider implements LoginProvider {
  final Auth0Web auth0;
  final String redirectUrl;

  WebLoginProvider({
    required this.auth0,
    required this.redirectUrl,
  });

  @override
  Future<UserProfile?> silentLogin() async {
    final credentials = await auth0.onLoad();
    return credentials?.user;
  }

  @override
  Future<UserProfile?> login() async {
    await auth0.loginWithRedirect(redirectUrl: redirectUrl);
    return null;
  }

  @override
  Future logout() {
    return auth0.logout(returnToUrl: redirectUrl);
  }
}

class NativeLoginProvider implements LoginProvider {
  final Auth0 auth0;
  final String scheme;

  NativeLoginProvider({
    required this.auth0,
    required this.scheme,
  });

  @override
  Future<UserProfile?> silentLogin() {
    return Future.value(null);
  }

  @override
  Future<UserProfile?> login() async {
    final credentials = await auth0
        .webAuthentication(scheme: scheme)
        .login();

    return credentials.user;
  }

  @override
  Future logout() async {
    return auth0
        .webAuthentication(scheme: scheme)
        .logout();
  }
}