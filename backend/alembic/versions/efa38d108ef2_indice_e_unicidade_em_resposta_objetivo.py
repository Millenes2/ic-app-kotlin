"""indice e unicidade em resposta_objetivo

Revision ID: efa38d108ef2
Revises: 5d45b487957e
Create Date: 2026-07-27 20:33:10.841321

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = 'efa38d108ef2'
down_revision: Union[str, Sequence[str], None] = '5d45b487957e'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Upgrade schema."""
    with op.batch_alter_table('resposta_objetivo') as batch_op:
        batch_op.create_index(
            op.f('ix_resposta_objetivo_usuario_id'), ['usuario_id'], unique=False
        )
        batch_op.create_unique_constraint(
            'uq_resposta_objetivo_usuario_objetivo_etapa', ['usuario_id', 'objetivo', 'etapa']
        )


def downgrade() -> None:
    """Downgrade schema."""
    with op.batch_alter_table('resposta_objetivo') as batch_op:
        batch_op.drop_constraint(
            'uq_resposta_objetivo_usuario_objetivo_etapa', type_='unique'
        )
        batch_op.drop_index(op.f('ix_resposta_objetivo_usuario_id'))
