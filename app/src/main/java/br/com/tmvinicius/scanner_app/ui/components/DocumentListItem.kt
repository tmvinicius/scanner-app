package br.com.tmvinicius.scanner_app.ui.components

import android.content.res.Configuration
import android.text.format.Formatter
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.tmvinicius.scanner_app.R
import br.com.tmvinicius.scanner_app.ui.sample.SampleDocuments
import br.com.tmvinicius.scanner_app.ui.theme.ScannerTheme
import java.text.DateFormat
import java.util.Date

/** Linha da lista de documentos: miniatura, nome, data e tamanho. */
@Composable
fun DocumentListItem(
    document: DocumentItemUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            Text(text = document.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        supportingContent = {
            Text(text = documentDetails(document.createdAtMillis, document.sizeBytes))
        },
        leadingContent = { DocumentThumbnailPlaceholder() },
        modifier = modifier.clickable(onClick = onClick),
    )
}

/** Miniatura provisória; a primeira página real do PDF entra no módulo M4. */
@Composable
private fun DocumentThumbnailPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 44.dp, height = 56.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(4.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_document),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** "27/10/26 09:15 · 240 kB", no formato do idioma do aparelho. */
@Composable
private fun documentDetails(createdAtMillis: Long, sizeBytes: Long): String {
    val context = LocalContext.current
    val date = remember(createdAtMillis) {
        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(createdAtMillis))
    }
    val size = remember(sizeBytes) { Formatter.formatShortFileSize(context, sizeBytes) }
    return stringResource(R.string.document_details, date, size)
}

@Preview(name = "Claro", showBackground = true)
@Preview(name = "Escuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DocumentListItemPreview() {
    ScannerTheme(dynamicColor = false) {
        DocumentListItem(document = SampleDocuments.items.first(), onClick = {})
    }
}
