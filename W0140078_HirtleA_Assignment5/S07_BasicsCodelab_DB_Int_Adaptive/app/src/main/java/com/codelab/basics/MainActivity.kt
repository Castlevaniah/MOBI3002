package com.codelab.basics

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codelab.basics.ui.theme.BasicsCodelabTheme
import com.codelab.basics.R

/**
 * Futuristic Pokémon UI quick notes:
 * - Dark base + neon gradient top bar.
 * - “Holo” cards (rounded, thin border, slight elevation).
 * - Pill buttons for a modern look.
 * - Tertiary color = neon accent (icons/chevrons).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val DBtest = DBClass(this@MainActivity) // make one DB helper for this Activity
        Log.d("CodeLab_DB", "onCreate")

        setContent {
            // NOTE: forcing dark = easier to see the futuristic style while designing
            // later you can remove darkTheme=true to follow system
            BasicsCodelabTheme(darkTheme = true) {
                // Scaffold = page frame (top bar + body)
                Scaffold(
                    topBar = { FuturisticTopBar() }, // our neon header
                    containerColor = MaterialTheme.colorScheme.background // dark background from theme
                ) { innerPadding ->
                    // app content goes here
                    MyApp(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding), // make room for the top bar
                        DBtest = DBtest
                    )
                }
            }
        }
    }
}

@Composable
private fun FuturisticTopBar() {
    // make a left→right gradient: primary (Poké Blue) to tertiary (Neon)
    val grad = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,   // brand blue
            MaterialTheme.colorScheme.tertiary   // neon cyan
        )
    )
    // Surface lets us use tonal elevation for a subtle lift
    Surface(tonalElevation = 6.dp, shadowElevation = 0.dp) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(64.dp)                   // taller header = bold look
                .background(grad, RectangleShape)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // header title — readable on the gradient
            Text(
                text = "Pokédex",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun MyApp(
    modifier: Modifier = Modifier,
    DBtest: DBClass
) {
    // names = list of Pokémon from DB (state so UI reacts to changes)
    var names by remember { mutableStateOf(DBtest.findAll()) }
    // index = which Pokémon is selected (-1 means “show list page”)
    var index by remember { mutableIntStateOf(-1) }
    // helper that tells us if screen is compact (phone) or wide (tablet/desktop)
    val windowInfo = rememberWindowInfo()

    // refresh = function that re-reads from DB and updates state
    // important: this is a FUNCTION REFERENCE (no parentheses here)
    val refresh: () -> Unit = { names = DBtest.findAll() }

    // full-screen background using theme color
    Box(modifier.background(MaterialTheme.colorScheme.background)) {
        // compact? show either master or details. else show both.
        val compact = windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact
        if (compact) {
            // decide which page to show on phones
            val showMaster = index == -1 || index !in names.indices
            if (showMaster) {
                // pass refresh (no parentheses) so child can call it later
                ShowPageMaster(names, { index = it }, DBtest, refresh)
            } else {
                ShowPageDetails(name = names[index], index = index, updateIndex = { index = it })
            }
        } else {
            // on wide screens show a split view (list left, details right)
            if (names.isNotEmpty() && index !in names.indices) index = 0 // select first if none
            Row(Modifier.fillMaxSize().padding(12.dp)) {
                // left panel = list
                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp)) // rounded panel
                        .background(MaterialTheme.colorScheme.surfaceVariant) // subtle panel color
                        .padding(12.dp)
                ) {
                    ShowPageMaster(names, { index = it }, DBtest, refresh)
                }
                Spacer(Modifier.width(12.dp))
                // right panel = details
                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(12.dp)
                ) {
                    if (names.isNotEmpty() && index in names.indices) {
                        ShowPageDetails(name = names[index], index = index, updateIndex = { index = it })
                    }
                }
            }
        }
    }
}

@Composable
private fun ShowPageMaster(
    names: List<DataModel>,
    updateIndex: (index: Int) -> Unit,
    DBtest: DBClass,
    refresh: () -> Unit
) {
    // ask DB who has the highest access_count (favorite)
    val favId = remember(names) { DBtest.getMostAccessed() }
    val fav = remember(names) { if (favId != 0L) DBtest.getById(favId) else null }

    Column(Modifier.fillMaxSize()) {

        // show favorite at the top if we have one
        if (fav != null) {
            Card(
                shape = RoundedCornerShape(20.dp), // “holo card” look
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), // thin border
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        "Favorite Pokémon",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.tertiary // neon pop
                    )
                    Text(
                        "#${fav.getNumber()} ${fav.getName()} • Accesses: ${fav.getAccessCount()}",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // the scrollable list of Pokémon
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp) // avoid FAB/nav overlap
        ) {
            // key = item id so Compose recycles correctly
            itemsIndexed(items = names, key = { _, item -> item.getId() }) { pos, name ->
                HoloListCard {
                    // row content inside the “holo” card
                    CardContent(name, pos, updateIndex, DBtest, refresh)
                    Log.d("CodeLab_DB", "Row rendered: ${name.getName()}")
                }
            }
        }
    }
}

@Composable
private fun HoloListCard(content: @Composable ColumnScope.() -> Unit) {
    // reusable card style for rows: rounded + border + small elevation
    Card(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp), content = content)
    }
}

@Composable
private fun CardContent(
    name: DataModel,
    pos: Int,
    updateIndex: (index: Int) -> Unit,
    DBtest: DBClass,
    refresh: () -> Unit
) {
    // expanded = show extra details text inside the row when true
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            // animate row height when we expand/collapse details
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier
                .weight(1f)           // text area takes remaining width
                .padding(end = 12.dp)
        ) {
            // pill button to open details and bump access_count
            FilledTonalButton(
                onClick = {
                    updateIndex(pos)                // switch to details page
                    DBtest.incAccessCount(name.getId()) // +1 access in DB
                    refresh()                        // reload list so counts/favorite update
                    Log.d("CodeLab_DB", "Clicked: id=${name.getId()} ${name.getName()}")
                },
                shape = CircleShape, // pill look
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier
                    .height(40.dp)
                    .defaultMinSize(minWidth = 120.dp)
            ) {
                Text("Details $pos", fontWeight = FontWeight.SemiBold)
            }

            // big name, using brand primary color
            Text(
                text = name.getName(),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )

            // optional extra info when expanded
            if (expanded) {
                Text(
                    text = name.toString(), // toString has number/power/access/description
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // chevron button to toggle expanded text
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (expanded) stringResource(R.string.show_less) else stringResource(R.string.show_more),
                tint = MaterialTheme.colorScheme.tertiary // neon accent
            )
        }
    }
}

@Composable
private fun ShowPageDetails(
    name: DataModel,
    updateIndex: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    index: Int
) {
    val windowInfo = rememberWindowInfo()
    // details panel also uses the “holo card” style
    Card(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // full details for the selected Pokémon
            Text(
                name.toString(),
                color = MaterialTheme.colorScheme.onSurface
            )
            Log.d("CodeLab_DB", "ShowDetails: $name")

            // on phones: button to go back to list page
            if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact) {
                Button(
                    onClick = { updateIndex(-1) }, // -1 means show master list
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) { Text("Master") }
            }

            // simple next / prev navigation (uses neon accent)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { updateIndex(index + 1) }, // move to next row
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) { Text("Next") }

                if (index > 0) {
                    Button(
                        onClick = { updateIndex(index - 1) }, // move to previous row
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        )
                    ) { Text("Prev") }
                }
            }
        }
    }
}
