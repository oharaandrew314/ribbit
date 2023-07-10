import 'package:auth0_flutter/auth0_flutter.dart';
import 'package:auth0_flutter/auth0_flutter_web.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

class MainView extends StatefulWidget {
  final Auth0Web? auth0Web;
  final Auth0? auth0Native;
  final String? auth0NativeScheme;
  final String? redirectUrlWeb;

  const MainView({
    final Key? key,
    this.auth0Web,
    this.auth0Native,
    this.auth0NativeScheme,
    this.redirectUrlWeb
  }) : super(key: key);

  @override
  State<MainView> createState() => _MainViewState();
}

class _MainViewState extends State<MainView> {
  Credentials? _credentials;

  @override
  void initState() {
    super.initState();

    if (kIsWeb) {
      widget.auth0Web!.onLoad().then((final credentials) =>
        setState(() {
          print('foo');
          print(credentials);
          _credentials = credentials;
        }
      ));
    }
  }

  Future<void> login() async {
    try {
      if (kIsWeb) {
        return widget.auth0Web!.loginWithRedirect(redirectUrl: widget.redirectUrlWeb);
      }

      var credentials = await widget.auth0Native!
          .webAuthentication(scheme: widget.auth0NativeScheme)
          .login();

      setState(() {
        _credentials = credentials;
      });
    } catch (e) {
      print(e);
    }
  }
  Future<void> logout() async {
    try {
      if (kIsWeb) {
        await widget.auth0Web!.logout(returnToUrl: widget.redirectUrlWeb);
      } else {
        await widget.auth0Native!
            .webAuthentication(scheme: widget.auth0NativeScheme)
            .logout();
        setState(() {
          _credentials = null;
        });
      }
    } catch (e) {
      print(e);
    }
  }

  @override
  Widget build(BuildContext context) {
    final credentials = _credentials;

    final widget = (credentials == null) ?
      ElevatedButton(
          onPressed: login,
          child: const Text("Log in")
      ) : Column(
      children: [
        Text("You are ${credentials.user.name}"),
        ElevatedButton(
            onPressed: logout,
            child: const Text("Log out"))
      ],
    );

    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme
            .of(context)
            .colorScheme
            .inversePrimary,
        title: const Text("Ribbit"),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            widget
          ],
        ),
      ),
    );
  }
}