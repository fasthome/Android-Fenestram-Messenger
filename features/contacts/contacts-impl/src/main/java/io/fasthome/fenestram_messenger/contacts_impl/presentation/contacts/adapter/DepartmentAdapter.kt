package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.contacts_impl.R
import io.fasthome.fenestram_messenger.contacts_impl.databinding.DepartmentHeaderItemBinding
import io.fasthome.fenestram_messenger.contacts_impl.databinding.DepartmentItemBinding
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.ContactsViewItem
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.DepartmentViewItem
import io.fasthome.fenestram_messenger.util.*

class DepartmentAdapter(
    onContactClick: (contact: ContactsViewItem) -> Unit
) :
    AsyncListDifferDelegationAdapter<DepartmentViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(),
        AdapterUtil.adapterDelegatesManager(
            createHeaderAdapterDelegate(),
            createDepartmentAdapterDelegate(onContactClick)
        )
    )

fun createDepartmentAdapterDelegate(
    onContactClick: (contact: ContactsViewItem) -> Unit
) =
    adapterDelegateViewBinding<DepartmentViewItem.Division, DepartmentItemBinding>(
        DepartmentItemBinding::inflate,
    ) {

        bindWithBinding {
            val adapterContacts = ContactsAdapter(textColor = item.textColor, onItemClicked = {
                onContactClick(it)
            })
            binding.root.onClick {
                item.isOpen = !item.isOpen
                binding.ivArrow.setImageResource(if (item.isOpen) R.drawable.ic_arrow_down_20 else R.drawable.ic_arrow_right_20)
                adapterContacts.items = if (item.isOpen) item.employee else emptyList()
            }
            rvContacts.layoutManager = LinearLayoutManager(rvContacts.context, LinearLayoutManager.VERTICAL,false)
            rvContacts.adapter = adapterContacts
            item.textColor?.let { it1 -> tvDepartmentName.setTextColor(it1) }
            tvDepartmentName.setPrintableText(item.title)
            tvDepartmentWorkers.text = context.resources.getQuantityString(
                R.plurals.employee_quantity,
                item.employee.size,
                item.employee.size,
            )
        }
    }

fun createHeaderAdapterDelegate() =
    adapterDelegateViewBinding<DepartmentViewItem.Header, DepartmentHeaderItemBinding>(
        DepartmentHeaderItemBinding::inflate,
    ) {
        bindWithBinding {
            tvDepartment.setPrintableText(item.header)
        }
    }