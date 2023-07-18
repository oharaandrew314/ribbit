import 'package:flutter/material.dart';
import 'package:ui/controllers/principal.dart';

class ProfileButton extends StatelessWidget {
  final Principal? principal;
  final Function(BuildContext) doLogout;
  final Function(BuildContext) doLogin;
  final _popupMenu = GlobalKey<PopupMenuButtonState>();

  ProfileButton({
    required this.principal,
    required this.doLogout,
    required this.doLogin,
    Key? key
  }): super(key: key);

  @override
  Widget build(BuildContext context) {
    if (principal == null) {
      return ElevatedButton(
          onPressed: () => doLogin(context),
          child: const Text("Login")
      );
    }

    return PopupMenuButton(
        key: _popupMenu,
        itemBuilder: (context) => [
          PopupMenuItem(
              child: Text(principal!.subject)
          ),
          PopupMenuItem(
              child: const Text("Logout"),
              onTap: () => doLogout(context)
          )
        ],
        child: ElevatedButton(
          child: const Text("Profile"),
          onPressed: () => _popupMenu.currentState?.showButtonMenu(),
        )
    );
  }
}