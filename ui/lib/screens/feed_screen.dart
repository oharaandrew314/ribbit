import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:ui/controllers/dtos.dart';
import 'package:ui/controllers/login_provider.dart';
import 'package:ui/controllers/principal.dart';
import 'package:ui/controllers/ribbit_client.dart';
import 'package:ui/widgets/post.dart';
import 'package:ui/widgets/top_bar.dart';

class FeedScreen extends StatefulWidget {
  final OAuthLoginProvider provider;
  final RibbitClient client;

  const FeedScreen({required this.client, required this.provider, final Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _FeedScreenState();
}

class _FeedScreenState extends State<FeedScreen> {

  List<PostDtoV1> _posts = [];
  Principal? _principal;
  UserDtoV1? _profile;

  @override
  void initState() {
    super.initState();
    loadPosts();
    silentLogin();
  }

  Future silentLogin() async {
    final principal = widget.provider.getUser();

    if (principal == null) return;

    final profile = await widget.client.getProfile(principal.idToken);
    if (profile == null && context.mounted) {
      context.go('/login');
    }

    setState(() {
      _principal = principal;
      _profile = profile;
    });
  }

  Future loadPosts() async {
    final page = await widget.client.listPosts("frogs");
    setState(() {
      _posts = page.items;
    });
  }

  void doLogin() async {
    final principal = await widget.provider.login();
    final profile = await widget.client.getProfile(principal.idToken);
    if (profile == null && context.mounted) {
      context.go('/login');
    }

    setState(() {
      _principal = principal;
      _profile = profile;
    });

  }

  void doLogout() {
    print('logout');
    widget.provider.logout();
    setState(() {
      _principal = null;
      _profile = null;
    });
  }

  @override
  Widget build(BuildContext context) {
    final items =  ListView.builder(
      itemCount: _posts.length,
        itemBuilder: (context, index) {
          final item = _posts[index];
          return Post(post: item);
        }
    );

    return Scaffold(
      appBar: AppBar(
        title: const Text('Ribbit'),
        actions: [
          ProfileButton(
              profile: _profile,
              logout: doLogout,
              login: doLogin
          ),
        ],
      ),
      body: items,
    );
  }

}