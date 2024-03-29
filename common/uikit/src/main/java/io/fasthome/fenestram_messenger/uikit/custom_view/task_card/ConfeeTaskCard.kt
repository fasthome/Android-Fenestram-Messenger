package io.fasthome.fenestram_messenger.uikit.custom_view.task_card

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import io.fasthome.fenestram_messenger.uikit.R
import io.fasthome.fenestram_messenger.uikit.SpacingItemDecoration
import io.fasthome.fenestram_messenger.uikit.databinding.TaskCardBinding
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.android.drawable
import io.fasthome.fenestram_messenger.util.android.setColor
import io.fasthome.fenestram_messenger.util.android.setDrawableBackgroundColor
import io.fasthome.fenestram_messenger.util.android.setDrawableBackgroundStroke
import io.fasthome.fenestram_messenger.util.dp
import io.fasthome.fenestram_messenger.util.setPrintableText

class ConfeeTaskCard : LinearLayout {

    private val binding: TaskCardBinding
    private val participantAdapter = TaskCardParticipantAdapter()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr, 0)

    init {
        LayoutInflater.from(context).inflate(R.layout.task_card, this, true)
        binding = TaskCardBinding.bind(this)
        binding.rvParticipantAvatars.adapter = participantAdapter
        binding.rvParticipantAvatars.addItemDecoration(SpacingItemDecoration { index, _ ->
            if (index > 0) Rect((-5).dp, 0.dp, 0.dp, 0.dp)
            else Rect(0.dp, 0.dp, 0.dp, 0.dp)
        })

        binding.tvExecutorLabel.text = context.getString(R.string.task_card_executor_label, ":")
        binding.tvParticipantLabel.text = context.getString(R.string.task_card_participant_label, ":")
    }

    fun setState(state: ConfeeTaskCardState) = with(binding) {
        tvNumber.text = state.number
        tvTitle.text = state.title
        tvTitle.setTextColor(state.style.textColor)
        tvType.setPrintableText(state.taskType)
        root.background = context.drawable(state.style.background)
        root.setDrawableBackgroundColor(state.style.backgroundColor)
        root.setDrawableBackgroundStroke(state.priority.priorityColor)

        if (state.priority.priority != PrintableText.EMPTY) {
            tvPriority.isVisible = true
            tvPriority.setPrintableText(state.priority.priority)
            tvPriority.background.setColor(state.priority.priorityColor)
        } else {
            tvPriority.isVisible = false
        }

        tvStatus.setPrintableText(state.status.status)
        tvStatus.background.setColor(state.status.statusColor)

        tvCreatedAt.text = state.createdAt
        state.updatedAt?.let {
            tvUpdatedAt.isVisible = true
            ivDateArrow.isVisible = true
            tvUpdatedAt.text = it
        } ?: run {
            tvUpdatedAt.isVisible = false
            ivDateArrow.isVisible = false
        }

        tvCustomerLabel.setPrintableText(state.customerLabel)
        state.customer?.let {
            glUsers.isVisible = true
            tvCustomerName.text = it.name
            tvCustomerName.setTextColor(state.style.textColor)
            ivCustomerAvatar.loadAvatarWithGradient(state.customer)
        } ?: run {
            glUsers.isVisible = false
            return@with
        }
        state.executor?.let {
            tvExecutorLabel.isVisible = true
            ivExecutorAvatar.isVisible = true
            tvExecutorName.isVisible = true
            tvExecutorName.text = it.name
            tvExecutorName.setTextColor(state.style.textColor)
            ivExecutorAvatar.loadAvatarWithGradient(state.executor)
        } ?: run {
            tvExecutorLabel.isVisible = false
            ivExecutorAvatar.isVisible = false
            tvExecutorName.isVisible = false
        }
        state.participants?.let {
            tvParticipantLabel.isVisible = true
            rvParticipantAvatars.isVisible = true
            participantAdapter.items = state.participants
        } ?: run {
            tvParticipantLabel.isVisible = false
            rvParticipantAvatars.isVisible = false
        }
    }

}