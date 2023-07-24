import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:ui/controllers/dtos.dart';
import 'package:ui/controllers/login_provider.dart';
import 'package:ui/controllers/principal.dart';
import 'package:ui/controllers/ribbit_client.dart';
import 'package:ui/widgets/post.dart';
import 'package:ui/widgets/profile_button.dart';
import 'package:ui/widgets/sub_selector.dart';

class FeedScreen extends StatefulWidget {
  final OAuthLoginProvider provider;
  final RibbitClient client;

  const FeedScreen({required this.client, required this.provider, final Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _FeedScreenState();
}

class _FeedScreenState extends State<FeedScreen> {

  List<SubDtoV1> _subs = [];
  List<PostDtoV1> _posts = [];
  Principal? _principal;
  UserDtoV1? _profile;

  @override
  void initState() {
    super.initState();
    silentLogin();
    loadSubs();
  }

  Future silentLogin() async {
    final principal = widget.provider.getUser();

    if (principal == null) return;

    final profile = await widget.client.getProfile(principal);
    if (profile == null && context.mounted) {
      context.go('/login');
    }

    setState(() {
      _principal = principal;
      _profile = profile;
    });
  }

  Future loadSubs() async {
    final subs = await widget.client.listSubs();
    setState(() {
      _subs = subs.items;
    });
  }

  Future loadPosts(SubDtoV1 sub) async {
    final page = await widget.client.listPosts(sub.id);
    setState(() {
      _posts = page.items;
    });
  }

  void doLogin() async {
    final principal = await widget.provider.login();
    final profile = await widget.client.getProfile(principal);
    if (profile == null && context.mounted) {
      context.go('/login');
    }

    setState(() {
      _principal = principal;
      _profile = profile;
    });

  }

  void doLogout() {
    widget.provider.logout();
    setState(() {
      _principal = null;
      _profile = null;
    });
  }

  void doPost() {
    context.go('/new');
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
          if (_principal != null) ElevatedButton.icon(
              onPressed: doPost,
              label: const Text('Post'),
              icon: const Icon(Icons.add)
          ),
          SubSelector(
              subs: _subs,
              selected: _subs.firstOrNull,
              select: loadPosts,
          ),
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