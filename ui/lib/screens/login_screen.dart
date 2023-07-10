import 'package:auth0_flutter/auth0_flutter.dart';
import 'package:flutter/material.dart';
import 'package:ui/controllers/login_provider.dart';

import '../constants.dart';
import '../widgets/hero.dart';
import '../widgets/user.dart';

class LoginScreen extends StatefulWidget {
  final LoginProvider provider;

  const LoginScreen({required this.provider, final Key? key}) : super(key: key);

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  UserProfile? _user;

  @override
  void initState() {
    super.initState();
    silentLogin();
  }

  Future silentLogin() async {
    final user = await widget.provider.silentLogin();

    setState(() {
      _user = user;
    });
  }

  Future login() async {
    final user = await widget.provider.login();
    setState(() {
      _user = user;
    });
  }

  Future logout() async {
    await widget.provider.logout();
    setState(() {
      _user = null;
    });
  }

  @override
  Widget build(final BuildContext context) {
    final body = Padding(
      padding: const EdgeInsets.only(
        top: padding,
        bottom: padding,
        left: padding / 2,
        right: padding / 2,
      ),
      child: Column(crossAxisAlignment: CrossAxisAlignment.center, children: [
        Expanded(
            child: Row(children: [
              _user != null
                  ? Expanded(child: UserWidget(user: _user))
                  : const Expanded(child: HeroWidget())
            ])),
        _user != null
            ? ElevatedButton(
          onPressed: logout,
          style: ButtonStyle(
            backgroundColor:
            MaterialStateProperty.all<Color>(Colors.black),
          ),
          child: const Text('Logout'),
        )
            : ElevatedButton(
          onPressed: login,
          style: ButtonStyle(
            backgroundColor:
            MaterialStateProperty.all<Color>(Colors.black),
          ),
          child: const Text('Login'),
        )
      ]),
    );

    return MaterialApp(
      title: 'Ribbit',
      home: body
    );
  }
}
