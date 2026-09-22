package com.bedober.payme.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Entity(tableName = "wallets")
data class WalletEntity(
    @PrimaryKey val id: String,
    val name: String,
    val currency: String,
    val balance: Double,
    val isCrypto: Boolean,
    val color: Long
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val type: String,
    val title: String,
    val amount: Double,
    val currency: String,
    val counterparty: String,
    val status: String,
    val createdAt: Long
)

fun Wallet.toEntity() = WalletEntity(id, name, currency, balance, isCrypto, color)
fun WalletEntity.toModel() = Wallet(id, name, currency, balance, isCrypto, color)

fun Transaction.toEntity() = TransactionEntity(id, type.name, title, amount, currency, counterparty, status.name, createdAt)
fun TransactionEntity.toModel() = Transaction(
    id = id,
    type = TransactionType.valueOf(type),
    title = title,
    amount = amount,
    currency = currency,
    counterparty = counterparty,
    status = TransactionStatus.valueOf(status),
    createdAt = createdAt
)

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallets ORDER BY name ASC")
    fun observeWallets(): Flow<List<WalletEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWallet(wallet: WalletEntity)

    @Query("UPDATE wallets SET balance = :balance WHERE id = :id")
    suspend fun updateBalance(id: String, balance: Double)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    fun observeTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tx: TransactionEntity)
}

@Database(entities = [WalletEntity::class, TransactionEntity::class], version = 1, exportSchema = false)
abstract class PaymeDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var instance: PaymeDatabase? = null

        fun get(context: android.content.Context): PaymeDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                PaymeDatabase::class.java,
                "payme.db"
            ).fallbackToDestructiveMigration().build().also { instance = it }
        }
    }
}
