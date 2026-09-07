# CLAUDE.md — Projet Astro

Fichier de référence du projet. À maintenir à jour à chaque décision technique.

## 1. Décisions techniques (validées par l'utilisateur le 2026-09-06)

| Sujet | Décision | Statut |
|---|---|---|
| Plateforme cible | Android | Validé |
| Plateforme secondaire | Aucune pour l'instant (PC écarté) | Validé |
| Langage / UI | Kotlin + Jetpack Compose | Validé |
| Priorité | Android d'abord | Validé |
| Stockage | Local d'abord, derrière une abstraction permettant un cloud ultérieur | Validé |
| Authentification | Aucune au départ. Interface `AuthService` prévue, implémentation locale | Validé |
| Bibliothèque 3D | AUCUNE. Le dodécaèdre est dessiné en Compose Canvas (20 sommets, 30 arêtes) | Décidé |
| Direction artistique | Deux ambiances, design system maison, zéro dépendance graphique | Validé |

## 2. Environnement de développement (vérifié le 2026-09-06)

- Répertoire projet : `/mnt/c/Users/cgill/astro` (= `C:\Users\cgill\astro`)
- Shell agent : WSL2 Ubuntu. Interop vers Windows : **fonctionnel**.
- Android Studio : `AI-261.23567.138` (2026.1.1), JDK 21 embarqué (`.../Android Studio/jbr`).
- Android SDK : `C:\Users\cgill\AppData\Local\Android\Sdk` — plateformes `android-36.1`
  et `android-37.0`, build-tools `36.1.0` et `37.0.0`, `platform-tools` (adb), licences acceptées.
- `cmdline-tools` révision 23.0 installés manuellement dans `Sdk/cmdline-tools/latest`.
  Attention : `sdkmanager` y est **déprécié** au profit de la CLI `android` (même dossier).
- Piège `cmd.exe` : les `;` d'un nom de paquet SDK sont interprétés comme des séparateurs
  d'arguments. Passer par un fichier `.bat` intermédiaire, pas par `cmd.exe /c "..."`.
- AGP télécharge lui-même les plateformes manquantes, les licences étant acceptées —
  c'est ainsi qu'`android-37.0` a été obtenue lors du passage à `compileSdk 37`.
- Émulateur : image `system-images;android-36.1;google_apis_playstore;x86_64` (2,4 Go),
  AVD **`Astro_Pixel8_API36`** (profil Pixel 8), défini dans `C:\Users\cgill\.android\avd`.
  Image Play Store retenue pour disposer des services Google et du moteur de synthèse
  vocale Google, utiles aux fonctionnalités vocales.
- Pas de `sudo` sans mot de passe dans WSL. Pas de `java`, `gradle`, `adb` côté WSL.
- **La compilation se fait côté Windows** (`gradlew.bat` via `cmd.exe`), jamais dans WSL.
  Motif : JDK et SDK sont côté Windows, et seul Windows pilote l'émulateur ou un téléphone USB.
- Le projet a été généré par l'assistant Android Studio puis déplacé depuis
  `AndroidStudioProjects\astro`. Les caches `.gradle` et `.idea` de l'ancien chemin
  ont été supprimés (chemins absolus obsolètes). Ils se régénèrent à l'ouverture.

## 2 bis. Versions en place (générées par le template, vérifiées le 2026-09-06)

| Composant | Version | Origine |
|---|---|---|
| AGP | 9.2.1 | template Android Studio |
| Kotlin | 2.2.10 | template Android Studio |
| Gradle (wrapper) | 9.4.1 | template Android Studio |
| Compose BOM | 2026.08.00 | relevé Google Maven |
| androidx core-ktx | 1.19.0 | relevé Google Maven |
| androidx lifecycle (runtime + viewmodel-compose) | 2.11.0 | relevé Google Maven |
| androidx activity-compose | 1.13.0 | relevé Google Maven |
| androidx navigation-compose | 2.10.0 | relevé Google Maven |
| androidx datastore-preferences | 1.2.1 | relevé Google Maven |
| kotlinx-coroutines-test | 1.11.0 | relevé Maven Central |

AGP, Kotlin et Gradle sont laissés aux versions du template : les monter change le DSL
de build et se traite comme une étape à part entière, pas en passant.

Configuration du module `:app` : `namespace`/`applicationId` = `com.astro.app`,
`minSdk = 26`, `targetSdk = 36`, `compileSdk = 37`, Kotlin DSL,
catalogue de versions dans `gradle/libs.versions.toml`.

**Règle : toute dépendance passe par `gradle/libs.versions.toml`.** Aucune version
écrite en dur dans un `build.gradle.kts`.

**`compileSdk` est à 37 et non 36** : les versions androidx ci-dessus l'exigent
(erreur de build explicite sinon). `targetSdk` reste à 36, car le monter change le
comportement d'exécution de l'application et doit être testé sur appareil séparément.

## 3. Règles de développement obligatoires

1. **150 lignes maximum par fichier source.** Jamais de code compressé artificiellement pour tenir.
   Si un fichier dépasse : découper par responsabilité, pas raccourcir les noms.
2. **Aucune donnée inventée.** Pas d'API, de comportement, de champ ou de valeur non vérifié.
   Si une information manque : le signaler explicitement, ne pas combler.
3. **Aucune donnée métier ni configuration en dur.** Couleurs, phrases, textes, signes,
   libellés, seuils : tout vient de la configuration, de la base ou des préférences utilisateur.
4. **Tout est activable/désactivable** depuis les Paramètres, et l'état est persisté.
5. **Vérification après chaque étape importante** : le projet compile, rien n'est cassé, tests lancés.
   Ne jamais continuer silencieusement après une erreur : signaler problème, cause, tentative, solution.

## 4. Architecture

Module unique `:app` au départ, avec séparation stricte par packages.
Extraction en modules Gradle possible plus tard sans réécriture.

```
app/src/main/java/<pkg>/
  core/
    config/        constantes centralisées, registre des fonctionnalités
    data/          DataStore (préférences), Room (phrases, manifestations), repositories
    domain/        interfaces : AuthService, SpeechService, TtsService, AstroDataSource
    ui/            thème, composants partagés
  feature/
    home/  astrology/  phrases/  calendar/  settings/  manifestation/
  navigation/      graphe de navigation, dérivé du registre de fonctionnalités
```

Règle : une fonctionnalité = un package `feature/<nom>` + une entrée dans le registre.
Ajouter une fonctionnalité ne doit modifier aucun fichier existant hors du registre.

## 5. Pages (6) et points d'entrée depuis la Home

Barre de navigation basse à 5 boutons (d'après la maquette `maquette astro base.png`) :
`Home` · `astrologie` · `histor. phrase` · `J/S M/A` (calendrier) · `paramètres`.

| Page | Entrée depuis la Home | Maquettée |
|---|---|---|
| Home | — | Oui |
| Astrologie | la Lune | Oui |
| Historique des phrases | la phrase du jour | Oui |
| Calendrier | le calendrier | Non |
| Paramètres | le Soleil | Oui |
| Manifestation | le dodécaèdre 3D | Non |

Comportement Retour : le bouton/geste Retour d'Android ramène toujours sur Home.
Quitter le Calendrier réinitialise son affichage en mode Jour.
### Calendrier — règle de swipe (précisée par l'utilisateur le 2026-09-06)

Séquence : Jour → Semaine → Mois → Année.

**3 swipes rapides dans une fenêtre de 1500 ms** font passer au niveau suivant.
Mesure de la fenêtre : entre le premier et le dernier swipe de la salve.

Décisions complémentaires, prises faute de spécification et faciles à inverser :
- 3 swipes rapides dans le sens opposé reviennent au niveau précédent (sans quoi,
  arrivé à Année, on ne peut plus redescendre sans quitter la page) ;
- pas de bouclage : à Année on reste à Année, à Jour on reste à Jour ;
- un swipe en sens inverse annule la salve en cours ;
- un déclenchement remet le compteur à zéro : 6 swipes rapides valent 2 changements.

Valeurs dans `core/config/CalendarConfig.kt`, jamais dans l'écran.
Règle isolée dans `CalendarSwipeDetector`, sans dépendance à Android ni à l'horloge
(l'instant est fourni par l'appelant), donc couverte par des tests unitaires JVM.

## 5 bis. Page d'accueil — composition

Trois couches superposées :
1. `CelestialBackground` — dégradé de ciel, halos, étoiles ;
2. `DayBeadLayer` — les deux billes-journées ;
3. `HomeShortcuts` — Lune, capsule « Phrase du jour », dodécaèdre, Soleil.

Les positions viennent de `core/config/HomeLayoutConfig.kt`, en **fractions d'écran** :
`centerX` et `radius` sont des fractions de la largeur, `centerY` de la hauteur. Une
valeur hors de [0, 1] place volontairement un élément partiellement hors écran.

Chaque raccourci n'apparaît que si sa fonctionnalité est activée : couper l'Astrologie
fait disparaître la Lune, pas seulement l'entrée de la barre de navigation.

### Billes-journées

Une **bille-journée** est une sphère qui porte une période et son planning. Ce n'est
ni une carte ni un bouton : halo qui la décolle du fond, contour en dégradé qui suggère
la courbure, intérieur à peine plus clair que le ciel. Son contenu vit à l'intérieur
d'elle, donc elle est cliquable dans son ensemble, texte compris.

Une bille peut être plus grande que l'écran et déborder — c'est ce qui donne la
profondeur. Son contenu est donc ancré en fractions de son diamètre
(`PRIMARY_CONTENT_X/Y`, `SECONDARY_CONTENT_X/Y`) pour rester dans la partie visible :
le centre géométrique d'une bille n'est pas au centre de ce que l'utilisateur voit.

Aucune date n'est écrite dans l'interface : tout dérive du curseur temporel.
Appuyer sur une bille ouvre le planning de SA date, transmise par l'argument de route.

### Le dodécaèdre

**Trois comportements distincts :**
- un **appui** ouvre la page Manifestation ;
- un **maintien long** ramène la navigation temporelle sur la journée en cours, sans
  changer de temporalité ;
- **rester appuyé emballe le solide** : rotation multipliée par
  `SOLID_BOOST_FACTOR` (×20) et entrée en jeu d'un troisième axe, le roulis, à l'arrêt
  au repos. L'emballement se prolonge `SOLID_BOOST_TAIL_MILLIS` (3 s) après le
  relâchement, puis redescend. Les deux transitions passent par une rampe : sans elle,
  le saut de 1× à 20× serait un à-coup, en contradiction avec le reste des animations.

**Pourquoi une rotation pilotée image par image.** `rememberInfiniteTransition` a une
durée figée et ne peut pas changer de vitesse en cours de route. `rememberSolidRotation`
intègre donc l'angle à chaque image à partir du temps écoulé : la rotation reste
continue quand la vitesse varie, sans saut d'angle au moment du changement. C'est le raccourci qui permet de rentrer après s'être éloigné
  dans le temps, sans revenir swipe par swipe. Un retour haptique confirme le geste :
  l'écran change peu, rien d'autre ne signalerait qu'il a été pris en compte.

La date du jour est **injectée** dans `HomeViewModel` (`today: () -> LocalDate`) plutôt
que lue directement : sans cela le retour à aujourd'hui dépendrait de l'horloge système
et ne serait pas testable.

**Deux axes sont animés**, à des périodes non multiples l'une de l'autre (26 s et 41 s) :
le mouvement ne se répète jamais à l'identique et toutes les faces finissent par se
présenter. Avec un seul axe animé et une inclinaison figée, la figure se lisait comme
une image plate qui pivote — c'était le défaut corrigé le 2026-09-06.

La projection est **perspective** et non orthographique : les sommets proches sont plus
écartés que les lointains. Les mathématiques sont dans `DodecahedronProjection.kt`,
séparées du composant — calculer et dessiner sont deux responsabilités distinctes.

## 5 quater. Navigation temporelle

**Deux gestes, à ne jamais confondre :**

| Geste | Où | Effet |
|---|---|---|
| Un swipe simple | Home | Déplace le curseur DANS la temporalité courante |
| Maintien long sur le dodécaèdre | Home | Ramène le curseur sur la journée en cours |
| Salve de 3 swipes rapides | Calendrier | CHANGE de temporalité |

Cette séparation est structurelle : `TimeCursor.previous()/next()` d'un côté,
`TimeCursor.withScale()` de l'autre, et deux détecteurs distincts.

`core/time/` porte le tout — placé dans `core` et non dans le Calendrier, parce que la
Home s'en sert aussi :

| Fichier | Rôle |
|---|---|
| `TimeScale.kt` | Jour → Semaine → Mois → Année |
| `TimeCursor.kt` | Position dans le temps, avec normalisation de l'ancre |
| `PeriodLabels.kt` | Libellés d'une période, dans la langue résolue |
| `SwipeBurstDetector.kt` | Règle des 3 swipes rapides |
| `SwipeDirection.kt` | Sens d'un geste, en intention |

**Normalisation de l'ancre** : `TimeCursor` ramène toujours la date au DÉBUT de la
période — lundi, premier du mois, premier janvier. Sans cela, « mois précédent » depuis
le 31 mars donnerait le 28 février et l'aller-retour ne reviendrait pas au point de
départ. Un test le vérifie explicitement.

Convention de sens, appliquée partout (`onHorizontalSwipe`) : glisser vers la **droite**
va vers le passé, comme on retourne une page en arrière ; vers la **gauche**, vers l'avenir.

## 5 quinquies. Planning

`PlanningRepository` (interface) + `DataStorePlanningRepository` (local). La clé est la
date en **ISO 8601** : format stable et indépendant de la langue, donc un utilisateur qui
change la langue de son téléphone ne perd rien.

La route est paramétrée (`planning/{date}`) et la date arrive au ViewModel par
`SavedStateHandle`. Enregistrement automatique après 600 ms de pause de frappe : ni
bouton, ni écriture disque à chaque touche.

## 5 ter. Direction artistique et design system

**Règle fondatrice, posée par l'utilisateur le 2026-09-06 :** la maquette définit
*quoi* afficher et *où* se trouvent les interactions. Elle ne définit pas *comment*
l'application doit apparaître. Ne jamais reproduire la maquette telle quelle.

### Univers

Spatial, astral, mystique mais moderne. Élégant, jamais enfantin. Sobriété assumée :
peu d'étoiles, peu d'effets, peu d'animations, mais soignés.

### Le fond

**Ambiance nocturne : un noir uni, sans aucun dégradé** (demandé le 2026-09-07). Ni
halos, ni nappe de poussière — ce sont des dégradés visibles, incompatibles avec l'espace
profond recherché. Seul subsiste le grain, à la limite du perceptible, qui évite le
banding d'un aplat sombre sur un écran.

La couleur vient du rôle **`space`**, distinct de `skyTop` / `skyMid` / `skyBottom` :
ces derniers servent aussi aux billes, au solide 3D et à la barre de navigation, et les
détourner aurait changé ces éléments en même temps que le fond.

**Ambiance claire : inchangée.** Elle garde son dégradé d'aube, ses halos et sa
poussière. Le noir profond n'a de sens que pour la nuit ; l'appliquer partout aurait
supprimé la seconde ambiance.

**Champ d'étoiles** (`StarField`) : quinze au maximum, petites, d'éclat et de taille
variables, réparties au hasard avec une graine fixe. Elles dérivent **de la gauche vers
la droite**, en sept à vingt minutes de traversée d'écran, accélérées de 5 % — le
mouvement existe sans s'attraper du regard. Le sens commun donne au ciel une cohérence,
les vitesses inégales évitent l'effet de bloc qui se déplace d'un seul tenant. Une étoile
sortie par la droite revient par la gauche, donc le ciel ne se vide jamais.

L'accélération est un facteur (`SPEED_FACTOR`) et non une modification des durées de
traversée : l'écart au réglage d'origine reste lisible, et un nouvel ajustement se fait
au même endroit.

Les positions sont intégrées image par image à partir du temps écoulé, comme la rotation
du solide, et non animées entre deux bornes : une animation bouclée provoquerait un saut
à sa reprise. Coût mesuré : 0,20 % d'images saccadées, médiane 22 ms, identique à la
version précédente.

### Deux ambiances

L'ambiance n'est pas un réglage d'écran : elle est **déclarée dans le catalogue**
(`FeatureDefinition.ambiance`), au même endroit que le reste de la définition d'une page.

| Ambiance | Pages | Palette |
|---|---|---|
| `DARK` | Home, Calendrier, Paramètres | `AstroNightPalette` — « Nuit profonde » |
| `LIGHT` | Astrologie, Phrases, Manifestation | `AstroDawnPalette` — « Aube céleste » |

La nuit n'est pas un fond noir : c'est un bleu très désaturé qui se réchauffe vers le
bas, comme un horizon. L'aube n'est pas un fond blanc : c'est un crème chaud qui
bascule vers un lavande pâle — l'inverse exact, la lumière venant du haut.

Le passage d'une ambiance à l'autre est un **fondu** : chaque couleur est animée
séparément (`animateColorAsState`), barre de navigation comprise.

### Structure du design system — `core/designsystem/`

| Fichier | Rôle |
|---|---|
| `AstroPalette.kt` | Rôles de couleur + les deux palettes. **Seul endroit où une couleur est écrite.** |
| `AstroTypography.kt` | Familles de polices et hiérarchie typographique |
| `AstroDimens.kt` | Espacements, rayons, épaisseurs de trait, tailles d'icône |
| `AstroMotion.kt` | Durées, courbes, spécifications d'animation réutilisables |
| `AstroTheme.kt` | Locals, bascule d'ambiance, alimentation de `MaterialTheme` |
| `components/` | `CelestialBackground`, `AstroScreen`, `AstroCard`, `AstroNavBar`, `SectionLabel`, `screenReveal` |

Les composants ne demandent jamais une couleur : ils demandent un **rôle**. Les deux
palettes exposent les mêmes rôles, donc aucun composant ne sait s'il est en clair ou
en sombre. Ajouter une palette = ajouter une instance, aucun écran à modifier.

**Contrôle automatisable :** `grep -rn "Color(0x" app/src/main/java --include=*.kt`
ne doit rien retourner hors de `core/designsystem`.

Exception unique : `res/values/colors.xml` définit `window_background`, appliqué par le
système avant que Compose ne s'affiche. Sans lui, un fond blanc apparaît au lancement
et transparaît derrière la barre de navigation. Sa valeur suit `AstroNightPalette.skyTop`.

### Typographie

- **Cormorant Garamond** — titres. Serif à fort contraste, évoque les planches
  d'astronomie anciennes. Réservé aux grandes tailles : ses déliés sont fragiles en petit corps.
- **Space Grotesk** — interface et texte courant. Grotesque moderne, dessiné pour l'écran.

Les deux sont sous **SIL Open Font License 1.1** (copies dans `assets/licenses/`),
**embarquées dans l'APK** : aucun téléchargement à l'exécution, donc aucune police qui
change après coup. Ce sont des polices **variables** — les graisses viennent de l'axe
`wght`, ce qui évite un fichier par graisse.

`labelSmall` sert d'exergue : petites capitales très espacées, précédées d'un filet
(`SectionLabel`). C'est le motif récurrent qui rattache les écrans entre eux, et le
seul endroit où l'on emploie des capitales.

### Transitions entre pages

Le passage d'un écran à l'autre est un **fondu**, de la même durée que la bascule
d'ambiance (`AstroMotion.TRANSITION_MILLIS`) : la page et ses couleurs changent d'un
seul mouvement au lieu de se succéder.

Il s'applique à **toutes** les destinations. La demande citait quatre éléments — Lune,
Soleil, dodécaèdre, bille-journée — mais n'animer que quatre destinations sur six aurait
rendu la navigation incohérente, d'autant que l'Astrologie et les Paramètres sont aussi
atteints depuis la barre.

Vérifié par mesure : à 300 ms d'une transition vers l'Astrologie, le haut de l'écran vaut
R203 G196 B194, entre le noir de l'accueil (3, 4, 8) et le crème de l'Astrologie
(≈ 247, 237, 226) — une image qui n'appartient à aucune des deux pages.

### Animation

Trois règles : rien ne bouge vite ; le mouvement d'ambiance est à peine perceptible ;
une réaction à un geste est immédiate. Durées centralisées dans `AstroMotion` —
ralentir toute l'application se fait là.

### Matière plutôt que vectoriel

Décision du 2026-09-07 : la Home ne doit plus se lire comme un assemblage de formes
vectorielles. Les éléments décoratifs sont rendus comme des corps éclairés.

**Ce qui reste vectoriel, volontairement** : les 5 pictogrammes de navigation, l'icône
de l'application, et les surfaces qui portent du texte — capsule de la phrase du jour,
barre de navigation, cartes. Ce sont des éléments d'interface, pas des objets célestes.

**Une seule source de lumière** pour toute l'application, `AstroLight`, en haut à droite.
C'est ce qui fait que la Lune, le Soleil, les billes et le solide appartiennent à la même
scène. Un éclairage contradictoire entre deux astres se repère immédiatement.

**Matières calculées, pas de fichiers image.** Les tuiles de grain et de poussière sont
générées une fois puis répétées (`core/designsystem/texture/`). Ce choix n'est pas un
pis-aller : une tuile calculée suit la palette de l'ambiance courante, se décline par
graine — la matière d'une bille dérive de sa date — et n'ajoute aucun fichier au projet.
Pour passer un jour à des ressources WebP, seul `TextureTiles.kt` serait à remplacer.

**Pas de shader.** `RuntimeShader` exige l'API 33, le projet vise l'API 26 : il faudrait
maintenir deux implémentations du même effet pour un résultat qu'une tuile en cache
donne déjà. Aucun effet identifié ne le justifie aujourd'hui.

**Dodécaèdre : faces pleines dessinées au Canvas.** Un moteur 3D pour un seul petit
objet ajouterait une dépendance et une surface de rendu à intégrer dans la composition.
Une série d'images pré-rendues figerait l'éclairage, ne suivrait pas la palette et
pèserait en mémoire. Les 12 faces sont **déduites des normales** (permutations de
(0, ±φ, ±1) — piège : ce ne sont PAS celles de (0, ±1, ±φ), qui donnent des sommets non
coplanaires) et vérifiées par test.

**Mesure de performance** (émulateur, 8 s, animations actives, 60 Hz) :

| | Avant (vectoriel) | Après (matière) |
|---|---|---|
| Images saccadées | 1,63 % | 0,20 % |
| Temps d'image médian | 23 ms | 21 ms |
| 90e centile | 29 ms | 24 ms |
| Mémoire PSS | 81,7 Mo | 90,9 Mo |

La matière coûte 9 Mo et rend l'affichage plus rapide : les deux grands cercles tracés
en contour coûtaient plus que les remplissages qui les remplacent.

### Dépendances graphiques

**Aucune bibliothèque graphique, d'animation ou 3D n'a été ajoutée**, alors que
l'autorisation était donnée. Motifs vérifiés au cas par cas :
- une bibliothèque 3D pour un solide en rotation lente serait disproportionnée, et le
  rendu au Canvas est déjà satisfaisant et sans coût de démarrage ;
- Lottie supposerait des fichiers d'animation que le projet n'a pas : l'ajouter
  n'apporterait rien tant qu'aucun asset n'existe ;
- les polices téléchargeables (`ui-text-google-fonts`) introduiraient une dépendance
  réseau à l'exécution là où l'embarquement est plus fiable ;
- `Modifier.blur` n'existe qu'à partir de l'API 31, alors que le projet vise l'API 26 :
  les halos sont donc des dégradés radiaux, qui fonctionnent partout et coûtent moins.

Cette décision est à revoir si un besoin réel apparaît, pas par principe.

### Pièges rencontrés, à ne pas réintroduire

- Un halo dessiné plus grand que le cadre d'un composant **découpé en cercle** est
  tranché net et se lit comme un disque. Le dégradé doit s'éteindre au bord du cadre.
- Un croissant de lune se construit par **soustraction de chemins**
  (`PathOperation.Difference`), pas par remplissage pair-impair : ce dernier peint la
  différence symétrique, donc la part du disque d'ombre qui déborde est peinte aussi
  et la figure devient un anneau.

## 5 sexies. Éphémérides : où vit quoi

**Chaque astre porte ses propres horaires, sur la page qu'il ouvre.**

| Astre sur la Home | Ouvre | Contenu |
|---|---|---|
| La Lune | Astrologie | Texture de la Lune, lever et coucher, calendrier lunaire dépliable sur 2 mois |
| Le Soleil | Paramètres | Texture du Soleil, lever et coucher |

Les horaires de la Lune ne sont **pas** dans les Paramètres : ils appartiennent à
l'astre par lequel on y accède (corrigé le 2026-09-07).

**La matière des astres est partagée**, pas dupliquée. `core/designsystem/celestial/`
expose `MoonOrb` et `SunOrb`, composants purement visuels employés à la fois par les
raccourcis de la page d'accueil et par les écrans d'information : une seule définition
de la matière, donc une Lune et un Soleil identiques partout. Les boutons de la Home
n'ajoutent que l'interaction.

**Les couleurs des astres sont indépendantes de l'ambiance** (`CelestialMaterial`).
La Lune est de la roche grise : elle ne devient pas crème parce que la page est claire.
Faire dériver sa matière de la couleur du ciel effaçait tout son relief sur l'ambiance
claire, l'ombre devenant aussi pâle que la lumière. Seuls les halos restent teintés par
la palette — ce sont eux qui rattachent l'astre à l'ambiance.

**Le calendrier lunaire ne demande aucune coordonnée** : la phase est géocentrique,
identique partout sur Terre. Seuls les horaires de lever et de coucher dépendent du lieu.
Le calendrier reste donc consultable même sans position renseignée.

Les disques de phase (`MoonPhaseGlyph`) ne sont pas des cercles partiellement remplis :
le terminateur se projette comme une **ellipse** de demi-axe `R × |1 − 2k|`. Un simple
remplissage donnerait une forme fausse à toutes les phases intermédiaires.

### Ordre et conditions de la page Paramètres

Le Soleil est la première carte : c'est l'astre par lequel on ouvre cette page, ses
horaires viennent donc en tête.

Les fonctionnalités affichées vivent **dans la carte « Affichage »**, à la suite de
l'interrupteur de la barre et sans sous-titre : ce sont des réglages d'affichage, pas
une famille distincte.

**Elles n'apparaissent que si la barre de navigation est active.** Ces interrupteurs
décident du contenu de la barre ; sans barre, ils n'ont plus d'objet.

**Conséquence à connaître** : ces mêmes interrupteurs pilotent aussi les raccourcis de
la page d'accueil — la Lune, la capsule de la phrase, le dodécaèdre. Masquer la barre
retire donc aussi le moyen de les rallumer. Le cas s'est produit en test : quatre
fonctionnalités coupées et la barre masquée, la Home ne montrait plus que le Soleil et
les billes.

La sortie de secours tient à une garantie posée plus tôt : **les Paramètres ne sont pas
désactivables**, le Soleil y mène donc toujours, et la carte « Affichage » y est toujours
visible. Réactiver la barre fait revenir la section, et les fonctionnalités avec.
Sans cette garantie, l'utilisateur serait enfermé.

### Position de l'appareil — jamais enregistrée

Les Paramètres proposent « Utiliser ma position ». La position obtenue vit **en mémoire
seulement** (`core/location/SessionLocation`) : elle n'est écrite ni dans les
préférences ni ailleurs, et disparaît à la fermeture de l'application. L'interface le
dit explicitement plutôt que de le laisser deviner.

`ObservationPoints.resolve` arbitre : la position de session l'emporte sur les
coordonnées saisies, étant la demande la plus récente et la plus explicite. Fonction
pure, couverte par test.

La portée est applicative et non liée à un écran : la Lune et le Soleil doivent voir le
même lieu dans une même session.

**Aucune dépendance ajoutée.** `LocationManagerCompat` d'androidx.core, déjà présent,
gère les différences entre versions d'Android depuis l'API 26 — les services Google Play
ne sont pas nécessaires.

Les fournisseurs sont essayés **les uns après les autres jusqu'à obtenir un point**.
S'arrêter au premier disponible était un défaut : un appareil peut exposer un
fournisseur réseau muet alors que son GPS répond. Corrigé le 2026-09-07 après
constatation sur émulateur.

Vérification faite : avec Paris enregistré et une position de session à Marseille, les
horaires affichés sont ceux de Marseille, les champs enregistrés restent ceux de Paris,
et les coordonnées de session sont **absentes du fichier de préférences** (recherche
directe de leur représentation binaire).

## 5 octies. Couleur des planètes-jours

Réglée dans les Paramètres, elle teinte les billes-journées de l'accueil : halo, brume
interne et liseré. **Jamais le texte**, qui doit rester lisible quelle que soit la
couleur choisie.

Deux modes exclusifs, plus une exception :

| Réglage | Effet |
|---|---|
| Semaine et week-end | Une couleur du lundi au vendredi, une autre le samedi et le dimanche |
| Une couleur par jour | Une couleur distincte pour chacun des sept jours |
| Règles de mot | Si le planning du jour contient le mot, sa planète prend cette couleur |

**Ordre de priorité** : la première règle de mot qui correspond l'emporte sur le mode.
C'est une exception délibérée — elle signale une journée particulière sans changer le
réglage général.

`PlanetColorResolver` est une fonction pure, couverte par test. Les couleurs sont
stockées en ARGB entier, forme indépendante de Compose donc manipulable par la couche
de données et testable sans Android.

**Interprétation retenue** : « le texte » d'une journée est **son planning**, le seul
texte propre à une date. La comparaison ignore la casse mais **pas les accents** :
« reunion » ne déclenche pas une règle « réunion ». Choix assumé — une normalisation
partielle surprendrait davantage qu'une absence de normalisation.

**Format de stockage des règles** : la couleur en préfixe de **longueur fixe** (huit
chiffres hexadécimaux) suivie du mot, une règle par ligne. Aucun séparateur entre les
deux champs, donc aucun caractère du mot ne peut casser la lecture — un test le vérifie
avec un mot contenant `|`, `=`, `,` et `;`. Seuls les retours à la ligne sont retirés.

Palette et valeurs initiales dans `core/config/PlanetColorDefaults.kt`, nulle part
ailleurs. Une dernière pastille marquée d'un plus ouvre une **roue chromatique** qui
donne accès à toutes les couleurs ; elle affiche ensuite la teinte retenue lorsque
celle-ci ne figure pas dans la palette, sans quoi un choix personnalisé n'apparaîtrait
nulle part.

La roue est faite de deux dégradés superposés : un **balayage** pour la teinte, qui fait
le tour du cercle, et un **radial** blanc vers transparent pour la saturation. La
dessiner pixel par pixel donnerait le même résultat en coûtant bien plus cher. La
luminosité est réglée à part par un curseur : elle ne peut pas s'exprimer sur un disque
déjà occupé par la teinte et la saturation.

Les pastilles vont à la ligne (`FlowRow`) : neuf ne tiennent pas sur une seule rangée,
et les serrer écrasait la dernière — précisément celle qui ouvre la roue.

## 5 septies. Calcul des éphémérides

**Les horaires se calculent à partir des COORDONNÉES, pas du fuseau horaire.** Le fuseau
ne décide que de l'heure locale d'affichage. Deux villes du même fuseau mais à des
longitudes différentes n'ont pas le même lever de soleil. C'est pourquoi les Paramètres
demandent une latitude et une longitude, et non seulement un fuseau.

Sans coordonnées renseignées, les cartes affichent un état vide. Aucune position par
défaut n'est inventée : des horaires calculés pour un lieu arbitraire seraient faux tout
en paraissant vrais.

`core/astronomy/` — tout est calculé localement, aucun service interrogé :

| Fichier | Rôle |
|---|---|
| `JulianDay.kt` | Conversion temps civil ↔ jour julien |
| `SunPosition.kt` | Déclinaison, équation du temps, longitude apparente (NOAA) |
| `SunCalculator.kt` | Lever, midi solaire, coucher, durée du jour |
| `MoonPosition.kt` | Ascension droite, déclinaison, élongation (série tronquée de Meeus) |
| `MoonCalculator.kt` | Lever et coucher par balayage de la hauteur, phase |

**Vérification par propriétés physiques**, faute de pouvoir comparer à des éphémérides
publiées depuis ce projet. Ces propriétés sont fortes : une erreur de modèle les casse.
Soleil — symétrie autour du midi solaire, 12 h à l'équateur toute l'année, 12 h partout
à l'équinoxe, jour et nuit polaires au-delà du cercle arctique, midi solaire à Greenwich
à moins de 18 min de midi UTC. Lune — cycle des phases de 29,5 jours, **pleine lune qui
se lève au coucher du soleil**, lever qui recule d'environ 50 min par jour.

Contrôle externe ponctuel : Paris le 7 septembre 2026, lever 07:16 et coucher 20:21 en
heure locale, à deux minutes près des valeurs réelles.

Précisions assumées : environ une minute pour le Soleil, quelques minutes pour la Lune
(série tronquée à neuf termes en longitude).

`moonrise` ou `moonset` peuvent être nuls, **et ce n'est pas une anomalie** : la Lune se
lève environ 50 min plus tard chaque jour, si bien qu'un jour sur vingt-cinq environ elle
ne se lève pas, ou ne se couche pas, dans la journée civile.

Piège corrigé le 2026-09-07 : un champ de saisie numérique ne doit jamais être lié
directement à la valeur analysée. Dès « 48. » tapé, la valeur est reparsée puis
réaffichée « 48.0 » et le point disparaît sous les doigts. Le texte saisi se mémorise à
part de la valeur validée.

## 6. Limitations connues — à ne jamais présenter comme résolues

1. **Import d'une voix** : le moteur de synthèse vocale Android n'accepte pas un fichier audio
   importé comme voix. Réellement possible : choisir parmi les voix installées, installer un
   moteur TTS tiers, ou un service cloud de clonage vocal (payant). Choix non tranché.
2. **Voix masculine / féminine** : Android n'expose pas le genre d'une voix de façon fiable.
   Conception retenue : lister les voix réellement disponibles, l'utilisateur assigne
   lui-même une voix au libellé « masculine » et une au libellé « féminine ».
3. **API horoscope avec notes /5 (amitié, amour, travail)** : aucune API gratuite fiable
   confirmée à ce jour. À tester par requêtes réelles avant toute promesse.
   Interdiction de convertir un texte d'horoscope en note /5 sans règle définie et validée.
4. **Soleil / Lune** : lever/coucher du soleil et phases de lune sont calculables localement
   (calcul déterministe, pas de donnée inventée). Lever/coucher de lune : à trancher après
   vérification. Source à rendre configurable dans tous les cas.

## 7. Vérification

Compilation (depuis WSL), **commande vérifiée le 2026-09-06** :
`cmd.exe /c "cd /d C:\Users\cgill\astro && gradlew.bat assembleDebug"`

Dernier résultat constaté (2026-09-06, squelette modulaire en place) : `BUILD SUCCESSFUL`,
APK de 13 Mo dans `app/build/outputs/apk/debug/app-debug.apk`,
**34 tests unitaires exécutés, 0 échec, 0 erreur, 0 ignoré**.
Contrôle de la limite de 150 lignes : fichier le plus long = 105 lignes.

### Vérifications à l'exécution (émulateur, 2026-09-06)

Effectuées sur `Astro_Pixel8_API36`, application installée et pilotée par `adb` :

- démarrage sans plantage (aucune trace dans `logcat -b crash`) ;
- barre basse à 5 entrées, Manifestation accessible uniquement depuis la Home ;
- Accueil et Paramètres absents de la liste des interrupteurs (non désactivables) ;
- désactivation d'une fonctionnalité : disparition immédiate de la barre basse ET de la
  Home, dans la même session, sans redémarrage ;
- persistance vérifiée sur deux fonctionnalités coupées, après `am force-stop` puis relance ;
- seules les clés réellement modifiées sont écrites dans le DataStore : les valeurs par
  défaut ne sont pas persistées, conformément à l'intention ;
- bouton Retour : ramène sur la Home depuis les Paramètres et depuis le Calendrier ;
- Calendrier : 3 swipes rapides enchaînés font passer Jour → Semaine, **un seul**
  changement pour la salve ; sortie puis retour sur la page repartent bien en mode Jour.

Méthode de lecture du DataStore : `adb exec-out run-as com.astro.app cat
files/datastore/astro_settings.preferences_pb`. **Ne pas utiliser `adb shell`** pour
lire un binaire : la traduction `\n` → `\r\n` insère des octets et corrompt le fichier lu.

Point à surveiller : lors d'une première passe de tests menée sans réinitialisation
préalable, une lecture du DataStore a montré un état incohérent avec les gestes effectués.
Quatre séquences contrôlées ultérieures (`pm clear` puis gestes tracés un à un) n'ont
pas reproduit l'anomalie et ont toutes donné le résultat attendu. Cause non établie,
donc ni corrigée ni écartée : à revérifier si le symptôme réapparaît.
Prérequis : Android Studio fermé (il verrouille les fichiers et le démon Gradle).
Tests : `gradlew.bat test` (unitaires), `gradlew.bat connectedAndroidTest` (instrumentés, device requis).

Démarrage de l'émulateur (depuis WSL) :
`"$SDK/emulator/emulator.exe" -avd Astro_Pixel8_API36` (à lancer en arrière-plan)
Attente du démarrage : boucler sur `adb shell getprop sys.boot_completed` jusqu'à `1`.
`adb wait-for-device` rend la main alors que l'appareil est encore `offline` : insuffisant.
Installation : `adb install -r app\build\outputs\apk\debug\app-debug.apk`

### Clavier dans l'émulateur

Un AVD créé par `avdmanager` a **`hw.keyboard=no`** par défaut : le clavier du PC n'est
pas transmis à l'émulateur, seul le clavier tactile fonctionne. Corrigé le 2026-09-07 en
passant `hw.keyboard=yes` dans
`C:\Users\cgill\.android\avd\Astro_Pixel8_API36.avd\config.ini`
(sauvegarde : `config.ini.bak`). Le changement demande un redémarrage de l'émulateur.

Effet de bord : dès qu'Android détecte un clavier physique, il **masque** le clavier
tactile. Pour garder les deux :
`adb shell settings put secure show_ime_with_hard_keyboard 1`

Les deux réglages survivent aux redémarrages de l'émulateur. Ils seraient perdus par un
effacement des données de l'AVD.

Limite connue : `hw.keyboard.charmap=qwerty2`. Sur un clavier physique AZERTY, certains
caractères peuvent ne pas correspondre. Non vérifié — je ne peux pas appuyer sur des
touches physiques.

**Attention lors des tests** : `adb shell pm clear com.astro.app` efface les préférences
et les plannings. Les données survivent en revanche à un `am force-stop`, à une
réinstallation par `install -r` et à un arrêt brutal de l'émulateur (`adb emu kill`) —
les trois cas ont été vérifiés.

## 8. Hors crédit

Ce qui n'a pas été fait, et pourquoi. À traiter avant de considérer une section terminée.

### Points ambigus — décision prise par défaut, à confirmer

Pour chacun, une logique a été mise en place pour que l'application fonctionne, mais
elle n'a **pas** été validée par l'utilisateur. Ne pas la traiter comme acquise.

1. **Période portée par la bille secondaire.** Rien ne dit quelle date elle représente.
   Choix appliqué : la période **suivante**. L'alternative — la précédente — se change
   dans `DayBeadLayer` en remplaçant `cursor.next()` par `cursor.previous()`.
2. **Sens des swipes sur la Home.** Seul « revenir à la période précédente » a été
   spécifié. Choix appliqué : droite = passé, gauche = avenir. Le sens « avant » n'a
   jamais été demandé explicitement.
3. **Placement de la bille secondaire.** La maquette montre deux cercles partiellement
   visibles, un coupé par le haut et un par la droite. Une seule bille secondaire étant
   demandée, celle du bord droit a été retenue et repositionnée en `(0.92, 0.60)` pour
   que sa date reste lisible. Ce n'est pas la position exacte de la maquette.
4. **Cercle coupé par le haut : non rendu.** Deux billes seulement étaient demandées.
5. **Choix de la temporalité sur la Home.** Rien n'indique comment l'utilisateur passe
   de Jour à Semaine depuis la Home. Le curseur y est donc figé sur `TimeScale.DAY`.
   Le Calendrier a sa propre temporalité, qui se réinitialise en sortant de la page —
   les deux ne sont **pas** reliées, faute de spécification.
6. **Notes /5 de la page Astrologie.** La maquette indique « l'amour, l'argent,
   l'amitié » ; le cahier des charges dit « amitié, amour, travail ». Contradiction non
   tranchée, rien n'a été codé.

### Décision en attente

**Le Calendrier n'a aucun point d'entrée sur la page d'accueil.** La Home mène à
l'Astrologie (Lune), aux Phrases (capsule), à la Manifestation (dodécaèdre) et aux
Paramètres (Soleil) — mais le Calendrier n'existe que dans la barre de navigation.
Or celle-ci est désormais masquable depuis les Paramètres : **barre masquée, le
Calendrier devient inaccessible**.

Rien n'a été fait de soi-même : ajouter un élément à la Home reviendrait à modifier une
composition que l'utilisateur a demandé de ne pas déplacer arbitrairement. Trois issues
possibles, à trancher : ajouter un point d'entrée sur la Home, rendre le Calendrier
atteignable depuis un autre écran, ou empêcher de masquer la barre tant que le
Calendrier est actif.

### Non implémenté

- Contenu réel des 5 sections : Astrologie, Phrases, Manifestation restent des écrans
  d'attente ; Calendrier n'affiche pas encore de grille.
- **APIs horoscope, lune et soleil** : aucune n'a été testée par requête réelle. Aucune
  API gratuite fournissant les trois notes /5 n'est confirmée à ce jour.
- **Sources de données** de la phrase du jour et de l'heure du réveil : inexistantes,
  les deux affichent un état vide.
- **Compte utilisateur** : ni authentification, ni e-mail, ni mot de passe.
- **Signe astrologique par défaut** et ses deux modes de sélection.
- **Voix** : ni import, ni dictée, ni lecture. La limitation technique sur l'import
  d'une voix (section 6) reste entière et non tranchée.
- **Ambiances claires non retouchées** : Astrologie, Phrases et Manifestation gardent
  le rendu précédent. La refonte en matière s'est arrêtée à la Home, comme demandé,
  pour validation avant d'aller plus loin.
- **Aucune ressource WebP n'a été produite** : je n'ai aucun moyen de créer des images.
  Les matières sont calculées. Si des textures peintes sont souhaitées, il faudra les
  fournir — l'architecture les acceptera sans réécriture des écrans.
- Aucun **test instrumenté** n'a été écrit : les vérifications d'interface ont été
  faites à la main sur l'émulateur, par captures d'écran.

### Vérifié partiellement

- **Facteur d'accélération du dodécaèdre (×20)** : implémenté et centralisé, mais **non
  mesuré**. Deux captures `adb screencap` consécutives sont espacées de plusieurs
  centaines de millisecondes ; à cette cadence, tout indicateur de différence de pixels
  sature aussi bien au repos qu'à l'accélération. Le comportement a été constaté à
  l'œil sur les images, pas quantifié. À juger sur appareil réel.

### Vérifié seulement sur émulateur

Tout le rendu et toutes les interactions ont été validés sur `Astro_Pixel8_API36`.
**Rien n'a été vérifié sur un appareil réel** — ni les performances des animations, ni
le confort réel des gestes de swipe, ni le rendu des polices sur un écran physique.
