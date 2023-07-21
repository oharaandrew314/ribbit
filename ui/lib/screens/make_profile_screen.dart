import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:ui/controllers/login_provider.dart';
import 'package:ui/controllers/principal.dart';
import 'package:ui/controllers/ribbit_client.dart';

class MakeProfileScreen extends StatefulWidget {
  final RibbitClient client;
  final OAuthLoginProvider provider;

  const MakeProfileScreen({required this.client, required this.provider, final Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _MakeProfileScreenState();
}

class _MakeProfileScreenState extends State<MakeProfileScreen> {

  final _usernameFieldController = TextEditingController();
  final _formKey = GlobalKey<FormState>();
  late Principal _principal;

  @override
  void initState() {
    super.initState();
    final principal = widget.provider.getUser();
    if (principal == null) {
      context.go('/');
    }

    _principal = principal!;
  }

  void submit() async {
    final profile = await widget.client.createProfile(
        token: _principal.idToken,
        name: _usernameFieldController.value.text
    );
    if (profile != null && context.mounted) {
      context.go('/');
    }
  }

  @override
  void dispose() {
    super.dispose();
    _usernameFieldController.dispose();
  }

  @override
  Widget build(BuildContext context) {
    const title = Text("Please choose a username");

    // final usernameField = TextField(
    //   controller: usernameFieldController,
    //   decoration: const InputDecoration(
    //     hintText: 'Something awesome...'
    //   ),
    // );
    //
    // final dialog = Column(
    //   children: [
    //     title,
    //     usernameField
    //   ],
    // );

    final usernameFormField = TextFormField(
      controller: _usernameFieldController,
      decoration: const InputDecoration(
          hintText: 'Choose your username...'
      ),
      validator: (value) {
        if (value == null || value.isEmpty) {
          return 'Please enter some text';
        }
        return null;
      },
    );

    final createButton = ElevatedButton(
      onPressed: submit,
      child: const Text('Create Profile'),
    );

    final form = Form(
      key: _formKey,
      child: Column(
        children: [
          usernameFormField,
          createButton
        ],
      )
    );

    return Scaffold(
      appBar: AppBar(
        title: const Text('Welcome to Ribbit!'),
      ),
      body: form,
    );
  }
}