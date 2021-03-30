'use strict';
const MANIFEST = 'flutter-app-manifest';
const TEMP = 'flutter-temp-cache';
const CACHE_NAME = 'flutter-app-cache';
const RESOURCES = {
  "assets/AssetManifest.json": "8add73a1677d056224cd19f6a92301a1",
"assets/assets/account.svg": "ea675ac1c8d3a7285a7962f6958c5d1c",
"assets/assets/accountBlack.svg": "ea675ac1c8d3a7285a7962f6958c5d1c",
"assets/assets/accountSelect.svg": "ea675ac1c8d3a7285a7962f6958c5d1c",
"assets/assets/activity.svg": "6ced257d98e7e4ee58745e46682d526f",
"assets/assets/activitySelect.svg": "6ced257d98e7e4ee58745e46682d526f",
"assets/assets/admin.svg": "74af450a16e08d9100c02841c8e7cd46",
"assets/assets/approved.svg": "d3432c7d8796af21aef614067490435a",
"assets/assets/authorized.svg": "b574766f75e07b9bdefdee81d65d616b",
"assets/assets/Barack_Obama.pdf": "09ac03cad84116683495369142e61d9d",
"assets/assets/borrowers.svg": "ce9ccf3236dc005af4f1f98a9683f38b",
"assets/assets/cashRegister.svg": "90ba8dc49dea6baa90534cc130c93833",
"assets/assets/coinImage.jpg": "5ac509aa32896561644a206f2657caf6",
"assets/assets/customer.svg": "26f6a773184d46c6670ca21efceb3687",
"assets/assets/dashboard.svg": "e8218df4e44a1cd48c62fbec9bbabf0d",
"assets/assets/dashboardSelect.svg": "e8218df4e44a1cd48c62fbec9bbabf0d",
"assets/assets/disburse.svg": "c900adc8636861e3b9b7e4cff96d278c",
"assets/assets/disburseBlack.svg": "c900adc8636861e3b9b7e4cff96d278c",
"assets/assets/disburseSelect.svg": "c900adc8636861e3b9b7e4cff96d278c",
"assets/assets/expenses.svg": "a68cadd4e2cd6f99d959e4621d1d6e9b",
"assets/assets/expensesIconBlack.svg": "a68cadd4e2cd6f99d959e4621d1d6e9b",
"assets/assets/expensesSelect.svg": "a68cadd4e2cd6f99d959e4621d1d6e9b",
"assets/assets/importMembers.svg": "e7c75111bd2bed7eafd5a074f3e3c088",
"assets/assets/loanType.svg": "b6de564b857880aa20a3d8bb5eafd3fc",
"assets/assets/logout.svg": "1a5bd42f5dbff19dc9f336a02bdffe9c",
"assets/assets/member.svg": "28f3555c53a8166fd58940039be76dfa",
"assets/assets/members.svg": "31f258ce8d93e83603df5ab76d4bda9a",
"assets/assets/memberSelect.svg": "28f3555c53a8166fd58940039be76dfa",
"assets/assets/menu.svg": "820124e9c560f84aeadbc4da474ba7ca",
"assets/assets/password.svg": "0ddc1e94ca6b7e7793d20ac8b3af3327",
"assets/assets/profile.svg": "31ce89ddc6e7af6b59977800f44702bd",
"assets/assets/refund.svg": "212960b44bf99b2a16554566e4c881d6",
"assets/assets/reports.svg": "a10a72b411963b93dafe083371ae3fe1",
"assets/assets/request.svg": "f20f85856584fc022288ac77b3f2c8fc",
"assets/assets/search.svg": "67f82549a0c8ad3a3c97160014bf1065",
"assets/assets/test.html": "4ee7c72baefd39d1693df660ed0ca268",
"assets/assets/username.svg": "b6c907597490e15e371e1b916dc3aba5",
"assets/assets/webIcon.png": "384520cce715b1e3fb38310ce7ebcbeb",
"assets/FontManifest.json": "dc3d03800ccca4601324923c0b1d6d57",
"assets/fonts/MaterialIcons-Regular.otf": "1288c9e28052e028aba623321f7826ac",
"assets/NOTICES": "909bbd58f5364f7a3a00dc7877becd73",
"assets/packages/cupertino_icons/assets/CupertinoIcons.ttf": "6d342eb68f170c97609e9da345464e5e",
"favicon.png": "384520cce715b1e3fb38310ce7ebcbeb",
"icons/Icon-192.png": "ac9a721a12bbc803b44f645561ecb1e1",
"icons/Icon-512.png": "96e752610906ba2a93c65f8abe1645f1",
"index.html": "aeae106672fa7e5aff3604711e3ae70e",
"/": "aeae106672fa7e5aff3604711e3ae70e",
"main.dart.js": "a6380d1a3a8d8586ee566d96c68d6d59",
"manifest.json": "e718771eee720531a41189805b789b77",
"pdfFunction.js": "a3c628e9c35bd599f15761ba98d20ebd",
"splash/style.css": "71533778dc93ea83ad31bb3835b3f48b",
"version.json": "34f343e0052c946c4cc166ba681fed11"
};

// The application shell files that are downloaded before a service worker can
// start.
const CORE = [
  "/",
"main.dart.js",
"index.html",
"assets/NOTICES",
"assets/AssetManifest.json",
"assets/FontManifest.json"];
// During install, the TEMP cache is populated with the application shell files.
self.addEventListener("install", (event) => {
  self.skipWaiting();
  return event.waitUntil(
    caches.open(TEMP).then((cache) => {
      return cache.addAll(
        CORE.map((value) => new Request(value + '?revision=' + RESOURCES[value], {'cache': 'reload'})));
    })
  );
});

// During activate, the cache is populated with the temp files downloaded in
// install. If this service worker is upgrading from one with a saved
// MANIFEST, then use this to retain unchanged resource files.
self.addEventListener("activate", function(event) {
  return event.waitUntil(async function() {
    try {
      var contentCache = await caches.open(CACHE_NAME);
      var tempCache = await caches.open(TEMP);
      var manifestCache = await caches.open(MANIFEST);
      var manifest = await manifestCache.match('manifest');
      // When there is no prior manifest, clear the entire cache.
      if (!manifest) {
        await caches.delete(CACHE_NAME);
        contentCache = await caches.open(CACHE_NAME);
        for (var request of await tempCache.keys()) {
          var response = await tempCache.match(request);
          await contentCache.put(request, response);
        }
        await caches.delete(TEMP);
        // Save the manifest to make future upgrades efficient.
        await manifestCache.put('manifest', new Response(JSON.stringify(RESOURCES)));
        return;
      }
      var oldManifest = await manifest.json();
      var origin = self.location.origin;
      for (var request of await contentCache.keys()) {
        var key = request.url.substring(origin.length + 1);
        if (key == "") {
          key = "/";
        }
        // If a resource from the old manifest is not in the new cache, or if
        // the MD5 sum has changed, delete it. Otherwise the resource is left
        // in the cache and can be reused by the new service worker.
        if (!RESOURCES[key] || RESOURCES[key] != oldManifest[key]) {
          await contentCache.delete(request);
        }
      }
      // Populate the cache with the app shell TEMP files, potentially overwriting
      // cache files preserved above.
      for (var request of await tempCache.keys()) {
        var response = await tempCache.match(request);
        await contentCache.put(request, response);
      }
      await caches.delete(TEMP);
      // Save the manifest to make future upgrades efficient.
      await manifestCache.put('manifest', new Response(JSON.stringify(RESOURCES)));
      return;
    } catch (err) {
      // On an unhandled exception the state of the cache cannot be guaranteed.
      console.error('Failed to upgrade service worker: ' + err);
      await caches.delete(CACHE_NAME);
      await caches.delete(TEMP);
      await caches.delete(MANIFEST);
    }
  }());
});

// The fetch handler redirects requests for RESOURCE files to the service
// worker cache.
self.addEventListener("fetch", (event) => {
  if (event.request.method !== 'GET') {
    return;
  }
  var origin = self.location.origin;
  var key = event.request.url.substring(origin.length + 1);
  // Redirect URLs to the index.html
  if (key.indexOf('?v=') != -1) {
    key = key.split('?v=')[0];
  }
  if (event.request.url == origin || event.request.url.startsWith(origin + '/#') || key == '') {
    key = '/';
  }
  // If the URL is not the RESOURCE list then return to signal that the
  // browser should take over.
  if (!RESOURCES[key]) {
    return;
  }
  // If the URL is the index.html, perform an online-first request.
  if (key == '/') {
    return onlineFirst(event);
  }
  event.respondWith(caches.open(CACHE_NAME)
    .then((cache) =>  {
      return cache.match(event.request).then((response) => {
        // Either respond with the cached resource, or perform a fetch and
        // lazily populate the cache.
        return response || fetch(event.request).then((response) => {
          cache.put(event.request, response.clone());
          return response;
        });
      })
    })
  );
});

self.addEventListener('message', (event) => {
  // SkipWaiting can be used to immediately activate a waiting service worker.
  // This will also require a page refresh triggered by the main worker.
  if (event.data === 'skipWaiting') {
    self.skipWaiting();
    return;
  }
  if (event.data === 'downloadOffline') {
    downloadOffline();
    return;
  }
});

// Download offline will check the RESOURCES for all files not in the cache
// and populate them.
async function downloadOffline() {
  var resources = [];
  var contentCache = await caches.open(CACHE_NAME);
  var currentContent = {};
  for (var request of await contentCache.keys()) {
    var key = request.url.substring(origin.length + 1);
    if (key == "") {
      key = "/";
    }
    currentContent[key] = true;
  }
  for (var resourceKey of Object.keys(RESOURCES)) {
    if (!currentContent[resourceKey]) {
      resources.push(resourceKey);
    }
  }
  return contentCache.addAll(resources);
}

// Attempt to download the resource online before falling back to
// the offline cache.
function onlineFirst(event) {
  return event.respondWith(
    fetch(event.request).then((response) => {
      return caches.open(CACHE_NAME).then((cache) => {
        cache.put(event.request, response.clone());
        return response;
      });
    }).catch((error) => {
      return caches.open(CACHE_NAME).then((cache) => {
        return cache.match(event.request).then((response) => {
          if (response != null) {
            return response;
          }
          throw error;
        });
      });
    })
  );
}
