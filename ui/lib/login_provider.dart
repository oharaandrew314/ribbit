import 'package:auth0_flutter/auth0_flutter.dart';
import 'package:auth0_flutter/auth0_flutter_web.dart';

abstract class LoginProvider {
  Future login();
  Future logout();
}

class WebLoginProvider implements LoginProvider {
  final Auth0Web auth0;
  final String clientId;
  final String domain;
  final String redirectUrl;

  WebLoginProvider({
    required this.auth0,
    required this.clientId,
    required this.domain,
    required this.redirectUrl,
  });

  @override
  Future login() {
    return auth0.loginWithRedirect(redirectUrl: redirectUrl);
  }

  @override
  Future logout() {
    return auth0.logout(returnToUrl: redirectUrl);
  }
}

class NativeLoginProvider implements LoginProvider {
  final Auth0 auth0;
  final String clientId;
  final String domain;
  final String scheme;

  NativeLoginProvider({
    required this.auth0,
    required this.clientId,
    required this.domain,
    required this.scheme,
  });

  @override
  Future login() async {
    await auth0
        .webAuthentication(scheme: scheme)
        .login();
  }

  @override
  Future logout() async {
    return auth0
        .webAuthentication(scheme: scheme)
        .logout();
  }
}