import 'package:flutter/material.dart';
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

  @override
  void initState() {
    super.initState();
    loadPosts();
    silentLogin();
  }

  Future silentLogin() async {
    final principal = await widget.provider.getUser();
    setState(() {
      _principal = principal;
    });
  }

  Future loadPosts() async {
    final page = await widget.client.listPosts("frogs");
    setState(() {
      _posts = page.items;
    });
  }

  void doLogin(BuildContext context) async {
    final principal = await widget.provider.login();
    setState(() {
      _principal = principal;
    });

  }

  void doLogout(BuildContext context) {
    widget.provider.logout();
    setState(() {
      _principal = null;
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
              principal: _principal,
              doLogout: doLogout,
              doLogin: doLogin
          ),
        ],
      ),
      body: items,
    );
  }

}