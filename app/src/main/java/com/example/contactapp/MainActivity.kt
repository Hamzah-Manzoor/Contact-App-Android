package com.example.contactapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



//val myMessageIcon: ImageVector
//    get() = Icons.Message




class MainActivity : AppCompatActivity() {
    private lateinit var contactViewModel: ContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check for READ_CONTACTS permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is already granted
            initializeViewModel()
        } else {
            // Request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                ContactViewModel.PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun initializeViewModel() {
        // Inside your activity or wherever you need the database instance
        val yourAppDatabaseInstance: AppDatabase = (application as MyApp).appDatabase


        // Initialize the ContactViewModel with the application context and database instance
        contactViewModel = ViewModelProvider(
            this,
            ContactViewModelFactory(applicationContext, yourAppDatabaseInstance) { this }
        ).get(ContactViewModel::class.java)

        // Set the content with the ContactScreenContent composable
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController, startDestination = "displayDataScreen") {
                        composable("displayDataScreen") {
                            DisplayDataScreen(
                                contacts = contactViewModel.allContacts.value ?: emptyList(),
                                onUpdateContact = { updatedContact ->
                                    // Implement the logic to update the contact
                                    contactViewModel.onUpdateContact(updatedContact)
                                },
                                onDeleteContact = { deletedContact ->
                                    // Implement the logic to delete the contact
                                    contactViewModel.onDeleteContact(deletedContact)
                                },
                                onCallContact = { contact ->
                                    // Implement the logic to initiate a call
                                    contactViewModel.onCallContact(contact)
                                },
                                onMessageContact = { contact ->
                                    // Implement the logic to send a message
                                    contactViewModel.onMessageContact(contact)
                                },
                                contactViewModel = contactViewModel,
                                onImportContactsClick = {
                                    navController.navigate("contactScreenContent")
                                }
                            )
                        }
                        composable("contactScreenContent") {
                            ContactScreenContent(
                                    contacts = contactViewModel.contacts,
                                    onContactSelected = { contact -> contactViewModel.toggleContactSelection(contact) },
                                    onImportAllContacts = { contactViewModel.importAllContacts() },
                                    onImportSelectedContacts = { contactViewModel.importSelectedContacts() },
                                    onSaveSelectedContacts = { contactViewModel.saveSelectedContactsToDatabase() },
                                    navController = navController
                            )
                        }
                    }
                    //Log.d("ContactApp", "Contacts: ${contactViewModel.contacts}")
                    //ContactScreenContent(
                    //    contacts = contactViewModel.contacts,
                    //    onContactSelected = { contact -> contactViewModel.toggleContactSelection(contact) },
                    //    onImportAllContacts = { contactViewModel.importAllContacts() },
                    //    onImportSelectedContacts = { contactViewModel.importSelectedContacts() },
                    //    onSaveSelectedContacts = { contactViewModel.saveSelectedContactsToDatabase() }
                    //)
//                    DisplayDataScreen(
//                        contacts = contactViewModel.allContacts.value ?: emptyList(),
//                        onUpdateContact = { updatedContact ->
//                            // Implement the logic to update the contact
//                            contactViewModel.onUpdateContact(updatedContact)
//                        },
//                        onDeleteContact = { deletedContact ->
//                            // Implement the logic to delete the contact
//                            contactViewModel.onDeleteContact(deletedContact)
//                        },
//                        onCallContact = { contact ->
//                            // Implement the logic to initiate a call
//                            contactViewModel.onCallContact(contact)
//                        },
//                        onMessageContact = { contact ->
//                            // Implement the logic to send a message
//                            contactViewModel.onMessageContact(contact)
//                        },
//                        contactViewModel = contactViewModel
//                    )
                }
            }
        }
    }
}


class ContactViewModelFactory(
    private val context: Context,
    private val database: AppDatabase,
    private val activityProvider: () -> Activity
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            return ContactViewModel(
                ImportContactsUseCase(context.contentResolver),
                context,
                database,
                activityProvider.invoke() // Invoke the lambda to get the activity when needed
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



data class Contact(
    val id: String,
    val name: String,
    val mobileNumber: String,
    val isSelected: Boolean = false // Optional: If you want to track selection status
)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreenContent(
    contacts: List<Contact>,
    onContactSelected: (Contact) -> Unit,
    onImportAllContacts: () -> Unit,
    onImportSelectedContacts: () -> Unit,
    onSaveSelectedContacts: () -> Unit,
    navController: NavHostController
) {
    Column {
        TopAppBar(
            title = { Text(text = "Contact Manager") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                IconButton(onClick = onImportAllContacts) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                }
                IconButton(onClick = onSaveSelectedContacts) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Save")
                }
            }
        )

        ContactList(contacts = contacts, onContactSelected = onContactSelected)
    }
}

@Composable
fun ContactList(contacts: List<Contact>, onContactSelected: (Contact) -> Unit) {
    LazyColumn {
        items(contacts) { contact ->
            ContactListItem(contact = contact, onContactSelected = onContactSelected)
        }
    }
}

@Composable
fun ContactListItem(contact: Contact, onContactSelected: (Contact) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Icon(imageVector = Icons.Default.Person, contentDescription = null)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "${contact.name} (${contact.mobileNumber})", modifier = Modifier.weight(1f))
        Checkbox(
            checked = contact.isSelected,
            onCheckedChange = { isChecked -> onContactSelected(contact.copy(isSelected = isChecked)) }
        )
    }
}


class ContactViewModel(
    private val importContactsUseCase: ImportContactsUseCase,
    private val context: Context,
    private val database: AppDatabase,
    private val activity: Activity,
    //LiveData or State for observing changes in the database
    val allContacts: LiveData<List<ContactEntity>> = database.contactDao().getAllContacts()
) : ViewModel() {

    // Initialize contacts with the result of importing all contacts
    var contacts by mutableStateOf(importContactsUseCase.importAllContacts())
    var isPermissionGranted by mutableStateOf(false)

    // Reference to the ContactDao
    private val contactDao: ContactDao = database.contactDao()

    init {
        // Additional initialization if needed
    }

    fun toggleContactSelection(contact: Contact) {
        contacts = contacts.map {
            if (it.id == contact.id) it.copy(isSelected = !it.isSelected) else it
        }
    }

    fun importAllContacts() {
        if (isPermissionGranted) {
            viewModelScope.launch {
                contacts = importContactsUseCase.importAllContacts()
            }
        } else {
            requestPermission()
        }
    }

    fun importSelectedContacts() {
        if (isPermissionGranted) {
            val selectedContacts = contacts.filter { it.isSelected }
            viewModelScope.launch {
                contacts = importContactsUseCase.importSelectedContacts(selectedContacts)
            }
        } else {
            requestPermission()
        }
    }

    fun saveSelectedContactsToDatabase() {
        viewModelScope.launch {
            val selectedContacts = contacts.filter { it.isSelected }
            withContext(Dispatchers.IO) {
                selectedContacts.forEach { contact ->
                    val contactEntity = ContactEntity(
                        id = contact.id,
                        name = contact.name,
                        mobileNumber = contact.mobileNumber
                    )
                    database.contactDao().insertContact(contactEntity)
                }
            }
            // Show a success message or handle UI accordingly
        }
    }

    // Handle permission result
    private fun requestPermission() {
        // Check if the permission is already granted
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            isPermissionGranted = true
        } else {
            // Request the permission
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    fun onUpdateContact(contact: ContactEntity) {
        viewModelScope.launch {
            // Update the contact in the Room database
            contactDao.updateContact(contact)
        }
    }

    fun onDeleteContact(contact: ContactEntity) {
        viewModelScope.launch {
            // Delete the contact from the Room database
            contactDao.deleteContact(contact)
        }
    }

    fun onCallContact(contact: ContactEntity) {
        // Use the activity context instead of the application context
        val phoneNumber = contact.mobileNumber
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK  // Add this line to set the flag
        activity.startActivity(intent)
    }




//    fun onMessageContact(contact: ContactEntity) {
//        val messageUri = Uri.parse("smsto:${contact.mobileNumber}")
//        val messageIntent = Intent(Intent.ACTION_SENDTO, messageUri)
//        messageIntent.putExtra("sms_body", "") // You can add a default message if needed
//        messageIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK  // Add this line to set the flag
//        context.startActivity(messageIntent)
//    }
    fun onMessageContact(contact: ContactEntity) {
        val messageUri = Uri.parse("smsto:${contact.mobileNumber}")
        val messageIntent = Intent(Intent.ACTION_SENDTO, messageUri)
        messageIntent.putExtra("sms_body", "") // You can add a default message if needed
        messageIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK  // Add this line to set the flag
        activity.startActivity(messageIntent)
    }



    companion object {
        const val PERMISSION_REQUEST_CODE = 123
    }
}

class ImportContactsUseCase(private val contentResolver: ContentResolver) {

    @SuppressLint("Range")
    fun importAllContacts(): List<Contact> {
        val contactsList = mutableListOf<Contact>()

        // Query contacts using ContentResolver
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val contactId = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                val contactName =
                    it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val mobileNo = getMobileNumber(contactId)

                val contact = Contact(id = contactId, name = contactName, mobileNumber = mobileNo)
                contactsList.add(contact)
            }
        }

        return contactsList
    }

    @SuppressLint("Range")
    private fun getMobileNumber(contactId: String): String {
        var mobileNumber = ""
        val phoneCursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
            arrayOf(contactId),
            null
        )

        phoneCursor?.use {
            if (it.moveToFirst()) {
                mobileNumber = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            }
        }

        return mobileNumber
    }

    fun importSelectedContacts(selectedContacts: List<Contact>): List<Contact> {
        // Implement the logic to import only the selected contacts
        // You can use the contact IDs from the selectedContacts list to filter the contacts

        // For demonstration purposes, we'll just return the selected contacts as is
        return selectedContacts
    }
}

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey val id: String,
    val name: String,
    val mobileNumber: String
)

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity)

    @Query("SELECT * FROM contacts")
    fun getAllContacts(): LiveData<List<ContactEntity>> // Use LiveData or Flow

    @Update
    suspend fun updateContact(contact: ContactEntity)

    @Delete
    suspend fun deleteContact(contact: ContactEntity)

}


@Database(entities = [ContactEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "MyContacts"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class MyApp : Application() {
    // AppDatabase instance
    lateinit var appDatabase: AppDatabase

    override fun onCreate() {
        super.onCreate()

        // Initialize your AppDatabase
        appDatabase = AppDatabase.getDatabase(this)
    }
}


@Composable
fun DisplayDataScreen(
    contacts: List<ContactEntity>,
    onUpdateContact: (ContactEntity) -> Unit,
    onDeleteContact: (ContactEntity) -> Unit,
    onCallContact: (ContactEntity) -> Unit,
    onMessageContact: (ContactEntity) -> Unit,
    contactViewModel: ContactViewModel,
    onImportContactsClick: () -> Unit
) {

    // Observe the LiveData using observeAsState
    val contacts: List<ContactEntity> by contactViewModel.allContacts.observeAsState(emptyList())

    Column {
        // Display a button to navigate to the Import Contact page
        //Button(onClick = { onImportContactsClick() }) {
        //    Text("Import Contacts")
        //}

        // Display data from the database
        if (contacts.isEmpty()) {
            Text("You don't have any saved contacts yet.")
        } else {
            LazyColumn {
                items(contacts) { contact ->
                    // Implement actions for updating, deleting, calling, and messaging
                    // You may use the provided lambda functions for these actions
                    ContactListItemMainPage(
                        contact = contact,
                        onUpdateContact = onUpdateContact,
                        onDeleteContact = onDeleteContact,
                        onCallContact = onCallContact,
                        onMessageContact = onMessageContact,
                        contactViewModel = contactViewModel
                    )
                }
            }
        }

        // Spacer to push the button to the bottom
        Spacer(modifier = Modifier.weight(1f))

        // Display a button to navigate to the Import Contact page
        Button(
            onClick = { onImportContactsClick() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Import Contacts")
        }
    }
}

@Composable
fun ContactListItemMainPage(
    contact: ContactEntity,
    onUpdateContact: (ContactEntity) -> Unit,
    onDeleteContact: (ContactEntity) -> Unit,
    onCallContact: (ContactEntity) -> Unit,
    onMessageContact: (ContactEntity) -> Unit,
    contactViewModel: ContactViewModel
) {
    var isUpdateDialogVisible by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Icon(imageVector = Icons.Default.Person, contentDescription = null)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "${contact.name} (${contact.mobileNumber})",
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = {
                isUpdateDialogVisible = true
            },
            modifier = Modifier
                .size(24.dp)
                .padding(4.dp)
        ) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Update")
        }
        IconButton(
            onClick = { onDeleteContact(contact) },
            modifier = Modifier
                .size(24.dp)
                .padding(4.dp)
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
        }
        IconButton(
            onClick = { onCallContact(contact) },
            modifier = Modifier
                .size(24.dp)
                .padding(4.dp)
        ) {
            Icon(imageVector = Icons.Default.Phone, contentDescription = "Call")
        }

        IconButton(
            onClick = { onMessageContact(contact) },
            modifier = Modifier
                .size(24.dp)
                .padding(4.dp)
        ) {
            Icon(imageVector = Icons.Default.MailOutline, contentDescription = "Message")
        }

        // Display the update contact pop-up if isUpdateDialogVisible is true
        if (isUpdateDialogVisible) {
            UpdateContactPopup(
                contact = contact,
                onUpdateContact = { updatedContact ->
                    onUpdateContact(updatedContact)
                    isUpdateDialogVisible = false
                },
                onDismiss = { isUpdateDialogVisible = false }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateContactPopup(
    contact: ContactEntity,
    onUpdateContact: (ContactEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var updatedName by remember { mutableStateOf(contact.name) }
    var updatedNumber by remember { mutableStateOf(contact.mobileNumber) }

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            color = Color.LightGray
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Update Contact", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = updatedName,
                    onValueChange = { updatedName = it },
                    label = { Text("Contact Name") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = updatedNumber,
                    onValueChange = { updatedNumber = it },
                    label = { Text("Contact Number") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            onUpdateContact(
                                contact.copy(
                                    name = updatedName,
                                    mobileNumber = updatedNumber
                                )
                            )
                            onDismiss()
                        }
                    ) {
                        Text("Update")
                    }
                }
            }
        }
    }
}







//@Preview(showBackground = true)
//@Composable
//fun ContactScreenPreview() {
//    val sampleContacts = listOf(
//        Contact(id = "1", name = "John Doe"),
//        Contact(id = "2", name = "Jane Doe"),
//        Contact(id = "3", name = "Bob Smith")
//    )
//
//    MaterialTheme {
//        ContactScreenContent(
//            contacts = sampleContacts,
//            onContactSelected = {},
//            onImportAllContacts = {},
//            onImportSelectedContacts = {}
//        )
//    }
//}


