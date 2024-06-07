import com.example.b09_wqga.database.User
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private val maxIdRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("maxId")

    suspend fun addUser(user: User) {
        val maxIdSnapshot = maxIdRef.get().await()
        val maxId = maxIdSnapshot.getValue(Int::class.java) ?: 0
        val newId = maxId + 1
        val newUser = user.copy(id = newId)
        database.child(newId.toString()).setValue(newUser).await()
        maxIdRef.setValue(newId).await()
    }

    suspend fun getUser(id: Int): User? {
        val snapshot = database.child(id.toString()).get().await()
        return snapshot.getValue(User::class.java)
    }

    suspend fun updateUser(user: User) {
        database.child(user.id.toString()).setValue(user).await()
    }

    suspend fun deleteUser(id: Int) {
        database.child(id.toString()).removeValue().await()
    }
}
