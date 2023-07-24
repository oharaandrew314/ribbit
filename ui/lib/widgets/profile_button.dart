import 'package:flutter/material.dart';
import 'package:ui/controllers/dtos.dart';

class ProfileButton extends StatelessWidget {
  final UserDtoV1? profile;
  final Function() logout;
  final Function() login;
  final _popupMenu = GlobalKey<PopupMenuButtonState>();

  ProfileButton({
    required this.profile,
    required this.logout,
    required this.login,
    Key? key
  }): super(key: key);

  @override
  Widget build(BuildContext context) {
    if (profile == null) {
      return ElevatedButton(
          onPressed: login,
          child: const Text("Login")
      );
    }

    return PopupMenuButton(
        key: _popupMenu,
        itemBuilder: (context) => [
          PopupMenuItem(
              onTap: logout,
              child: const Text("Logout"),
          )
        ],
        child: ElevatedButton(
          child: Text(profile!.name),
          onPressed: () => _popupMenu.currentState?.showButtonMenu(),
        )
    );
  }
}