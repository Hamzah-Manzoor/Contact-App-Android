import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.MyAndroidAppTheme

private val subtitle1: TextStyle
    get() {
        TODO();
    }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAndroidAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ContactListScreen()
                }
            }
        }
    }
}

data class Contact(
    val id: Long,
    val name: String,
    val phoneNumber: String
)

class ContactViewModel : ViewModel() {
    val contacts = mutableStateListOf<Contact>()
}

@Composable
fun fetchContacts(contacts: MutableList<Contact>) {
    // TODO: Implement contact retrieval using Content Resolver
    // Update the 'contacts' list with the retrieved contacts
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(contactViewModel: ContactViewModel = viewModel()) {
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        // Fetch contacts when the screen is launched
        //fetchContacts(contactViewModel.contacts)
    }

    Column {
        TopAppBar(
            title = {
                BasicTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        // TODO: Implement search functionality
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = subtitle1 // Fix: Use the correct Typography property
                )
            },
            navigationIcon = {
                // TODO: Implement navigation icon
            },
            actions = {
                // TODO: Implement actions
            }
        )

        // Display the list of contacts
        ContactList(contactViewModel.contacts)
    }
}

@Composable
fun ContactList(contacts: List<Contact>) {
    LazyColumn {
        items(contacts) { contact ->
            ContactItem(contact) // Fix: Pass the Contact object to ContactItem
            Divider(
                color = MaterialTheme.colorScheme.primary,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun ContactItem(contact: Contact) {
    // TODO: Implement the UI for displaying a contact item
    // You can use Card, Text, etc.
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyAndroidAppTheme {
        ContactListScreen()
    }
}
