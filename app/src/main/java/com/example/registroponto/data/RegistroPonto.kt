import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RegistroPonto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val data: String,
    val entrada: String?,
    val pausa: String?,
    val retorno: String?,
    val saida: String?
)
