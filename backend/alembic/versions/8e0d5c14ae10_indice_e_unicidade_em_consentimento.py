"""indice e unicidade em consentimento

Revision ID: 8e0d5c14ae10
Revises: efa38d108ef2
Create Date: 2026-07-27 21:23:46.534796

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '8e0d5c14ae10'
down_revision: Union[str, Sequence[str], None] = 'efa38d108ef2'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Upgrade schema."""
    with op.batch_alter_table('consentimento') as batch_op:
        batch_op.create_index(
            op.f('ix_consentimento_usuario_id'), ['usuario_id'], unique=False
        )
        batch_op.create_unique_constraint(
            'uq_consentimento_usuario_versao', ['usuario_id', 'versao_termos']
        )


def downgrade() -> None:
    """Downgrade schema."""
    with op.batch_alter_table('consentimento') as batch_op:
        batch_op.drop_constraint('uq_consentimento_usuario_versao', type_='unique')
        batch_op.drop_index(op.f('ix_consentimento_usuario_id'))
